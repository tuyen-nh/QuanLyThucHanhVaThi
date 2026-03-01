package com.example.agent.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Component
public class FileWatcherService {
    @Autowired
    private AiService aiService;

    @Autowired
    private NotificationService notificationService;

    @Value("${agent.watch.paths:C:/Users/*/Downloads}")
    private String watchPathsProp;

    private volatile boolean running = true;
    private Thread watcherThread;
    private java.util.Map<WatchKey, Path> watchKeys = new java.util.HashMap<>();
    private final java.util.Map<String, Long> processedFiles = new java.util.concurrent.ConcurrentHashMap<>();
    private final java.util.Set<String> tempSuffixes = java.util.Set.of(".crdownload", ".part", ".download", ".tmp",
            ".partial");

    @PostConstruct
    public void start() {
        watcherThread = new Thread(this::watchLoop, "FileWatcher");
        watcherThread.setDaemon(true);
        watcherThread.start();
    }

    private void watchLoop() {
        try {
            String[] parts = watchPathsProp.split(",");
            try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
                for (String p : parts) {
                    String trimmed = p.trim();
                    if (trimmed.isEmpty())
                        continue;
                    String normalized = trimmed.replace('\\', '/');
                    // Support common pattern: C:/Users/*/Downloads
                    if (normalized.startsWith("C:/Users/") && normalized.contains("*/")
                            && normalized.endsWith("/Downloads")) {
                        Path usersDir = Paths.get("C:/Users");
                        if (Files.exists(usersDir) && Files.isDirectory(usersDir)) {
                            try (DirectoryStream<Path> ds = Files.newDirectoryStream(usersDir)) {
                                for (Path user : ds) {
                                    if (!Files.isDirectory(user))
                                        continue;
                                    Path candidate = user.resolve("Downloads");
                                    registerPathIfExists(candidate, watcher);
                                }
                            }
                        }
                    } else {
                        Path dir = Paths.get(trimmed);
                        registerPathIfExists(dir, watcher);
                    }
                }

                while (running) {
                    WatchKey key = watcher.take();
                    Path dir = watchKeys.get(key);
                    if (dir == null) {
                        key.reset();
                        continue;
                    }

                    for (WatchEvent<?> ev : key.pollEvents()) {
                        WatchEvent.Kind<?> kind = ev.kind();
                        if (kind == StandardWatchEventKinds.OVERFLOW)
                            continue;

                        @SuppressWarnings("unchecked")
                        WatchEvent<Path> we = (WatchEvent<Path>) ev;
                        Path filename = we.context();
                        Path full = dir.resolve(filename);

                        if (isTempFileName(filename.toString())) {
                            // Lọc file rác: Bỏ qua ngay các file tạm đang tải dở (.tmp, .download,
                            // .crdownload,...)

                            continue;
                        }

                        // Chống spam (Debounce): Nếu file đó vừa mới được xử lý cách đây dưới 5 giây,
                        // bỏ qua để tránh quét lại
                        if (isProcessedRecently(full.toString())) {
                            continue;
                        }

                        // Chờ file ổn định (waitForStableFile): Đợi (tối đa 8s) cho đến khi kích thước
                        // file dừng thay đổi (tải xong)
                        if (waitForStableFile(full, 8000)) {
                            File f = full.toFile();
                            if (f.isFile()) {
                                try {
                                    // Quét và Báo động: Gọi AI scanFile, nếu là virus thì in cảnh báo và gửi thông
                                    // báo lên server
                                    boolean isMal = aiService.scanFile(f);
                                    if (isMal) {
                                        System.out.println("WARNING: Suspected malware: " + full);
                                        String detectionTime = LocalDateTime.now()
                                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                                        notificationService.sendNotification(
                                                "Please help check this file, suspected malware: " + full.toString(),
                                                full.toString(),
                                                f.getName(),
                                                detectionTime);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    key.reset();
                }
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerPathIfExists(Path dir, WatchService watcher) {
        try {
            if (Files.exists(dir) && Files.isDirectory(dir)) {
                WatchKey key = dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_MODIFY);
                watchKeys.put(key, dir);
                System.out.println("Watching: " + dir.toString());
            } else {
                System.out.println("Skip watch (not exists): " + dir);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // kiểm tra fila tạm thời như .tmp, .download, .crdownload,...
    private boolean isTempFileName(String name) {
        String lower = name.toLowerCase();
        for (String s : tempSuffixes)
            if (lower.endsWith(s))
                return true;
        return false;
    }

    private boolean waitForStableFile(Path p, long timeoutMs) throws InterruptedException {
        long start = System.currentTimeMillis();
        long prevSize = -1;
        while (System.currentTimeMillis() - start < timeoutMs) {
            try {
                if (!Files.exists(p)) {
                    TimeUnit.MILLISECONDS.sleep(200);
                    continue;
                }
                long size = Files.size(p);
                if (size > 0 && size == prevSize)
                    return true;
                prevSize = size;
            } catch (IOException ex) {
                // ignore and retry
            }
            TimeUnit.MILLISECONDS.sleep(300);
        }
        return Files.exists(p);
    }

    // tránh quét 1 file liên tiếp
    private boolean isProcessedRecently(String filePath) {
        long now = System.currentTimeMillis();
        long lastTime = processedFiles.getOrDefault(filePath, 0L);
        if (now - lastTime < 5000) { // 5 seconds debounce
            return true;
        }
        processedFiles.put(filePath, now);
        return false;
    }

    @PreDestroy
    public void stop() {
        running = false;
        if (watcherThread != null)
            watcherThread.interrupt();
    }
}

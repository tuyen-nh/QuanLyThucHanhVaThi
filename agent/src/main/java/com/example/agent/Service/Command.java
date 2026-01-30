package com.example.agent.Service;

<<<<<<< HEAD
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
=======
>>>>>>> c98faf91730db1699998a2a9b9f3871b99c96d9b
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
<<<<<<< HEAD

@Service
public class Command {
    @Value("${agent.api.key:}")
    private String apiKey;

    @Autowired
    private AiService aiService;

    private void sendProgressToServer(int percent) {
        try {
            URL serverUrl = new URL("http://localhost:8080/progress?percent=" + percent); // sửa theo URL dashboard
            HttpURLConnection conn = (HttpURLConnection) serverUrl.openConnection();
            conn.setRequestMethod("GET");
            if (apiKey != null && !apiKey.isBlank()) {
                conn.setRequestProperty("x-agent-key", apiKey);
            }
            conn.getResponseCode(); // Đọc để đảm bảo request được gửi đi
            conn.disconnect();
        } catch (IOException e) {
            System.err.println("Progress notify failed: " + e.getMessage());
        }
    }

    // downLoad software
=======
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class Command {
    private void sendProgressToServer(int percent) {
        try {
            URL serverUrl = new URL("http://localhost:8080/progress?percent=" + percent); // sửa theo URL dashboard của bạn
            HttpURLConnection conn = (HttpURLConnection) serverUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.getResponseCode(); // Đọc để đảm bảo request được gửi đi
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // downLoad sortware
>>>>>>> c98faf91730db1699998a2a9b9f3871b99c96d9b
    public void downloadAndInstall(String url) {
        try {
            String fileName = url.substring(url.lastIndexOf('/') + 1);
            String folderPath = "C:\\Temp";
            String savePath = folderPath + "\\" + fileName;

            File dir = new File(folderPath);
<<<<<<< HEAD
            if (!dir.exists())
                dir.mkdirs();
=======
            if (!dir.exists()) dir.mkdirs();
>>>>>>> c98faf91730db1699998a2a9b9f3871b99c96d9b

            // Tải file về
            URL downloadUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) downloadUrl.openConnection();
            long totalSize = connection.getContentLengthLong();

            InputStream in = connection.getInputStream();
            FileOutputStream out = new FileOutputStream(savePath);

            byte[] buffer = new byte[4096];
            int bytesRead;
            long downloaded = 0;
            int lastPercent = 0;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                downloaded += bytesRead;

                int percent = (int) ((downloaded * 100) / totalSize);
                if (percent != lastPercent) {
                    lastPercent = percent;
                    sendProgressToServer(percent);
                }
            }

            out.close();
            in.close();

<<<<<<< HEAD
            // Kiểm tra AI trước khi cài đặt
            try {
                boolean isMalware = aiService.scanFile(new File(savePath));
                if (isMalware) {
                    System.out.println("WARNING: Suspected malware detected, aborting install: " + savePath);
                    sendProgressToServer(0);
                    return;
                }
            } catch (Exception e) {
                // nếu AI gặp lỗi, ghi log ngắn và tiếp tục cài đặt
                System.err.println("AI scan failed: " + e.getMessage());
            }

            // Cài đặt trực tiếp
            String cmd = "powershell.exe Start-Process '" + savePath
                    + "' -ArgumentList '/VERYSILENT','/SUPPRESSMSGBOXES','/NORESTART','/SP-','/MERGETASKS=desktopicon' -Verb RunAs";
=======
            // Cài đặt trực tiếp
            String cmd = "powershell.exe Start-Process '" + savePath + "' -ArgumentList '/VERYSILENT','/SUPPRESSMSGBOXES','/NORESTART','/SP-','/MERGETASKS=desktopicon' -Verb RunAs";
>>>>>>> c98faf91730db1699998a2a9b9f3871b99c96d9b
            System.out.println("⚙️ Đang cài đặt: " + cmd);
            Process process = Runtime.getRuntime().exec(cmd);
            int exitCode = process.waitFor();
            System.out.println("✅ Cài đặt kết thúc với mã thoát: " + exitCode);
<<<<<<< HEAD
=======

>>>>>>> c98faf91730db1699998a2a9b9f3871b99c96d9b
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

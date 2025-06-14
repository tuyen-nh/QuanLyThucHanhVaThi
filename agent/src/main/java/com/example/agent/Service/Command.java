package com.example.agent.Service;

import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
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
    public void downloadAndInstall(String url) {
        try {
            String fileName = url.substring(url.lastIndexOf('/') + 1);
            String folderPath = "C:\\Temp";
            String savePath = folderPath + "\\" + fileName;

            File dir = new File(folderPath);
            if (!dir.exists()) dir.mkdirs();

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

            // Cài đặt trực tiếp
            String cmd = "powershell.exe Start-Process '" + savePath + "' -ArgumentList '/VERYSILENT','/SUPPRESSMSGBOXES','/NORESTART','/SP-','/MERGETASKS=desktopicon' -Verb RunAs";
            System.out.println("⚙️ Đang cài đặt: " + cmd);
            Process process = Runtime.getRuntime().exec(cmd);
            int exitCode = process.waitFor();
            System.out.println("✅ Cài đặt kết thúc với mã thoát: " + exitCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

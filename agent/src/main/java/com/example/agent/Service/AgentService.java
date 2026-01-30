<<<<<<< HEAD

=======
>>>>>>> c98faf91730db1699998a2a9b9f3871b99c96d9b
package com.example.agent.Service;

import com.example.agent.Model.AgentInfo;
import com.example.agent.Model.AgentInfo.Status;
import com.example.agent.Model.AgentInfo.FirewallStatus;

import org.springframework.stereotype.Service;
<<<<<<< HEAD
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

// CÁC IMPORT CẦN THIẾT ĐÃ ĐƯỢC THÊM
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
// import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;

import java.io.BufferedReader;
// import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.sql.Timestamp;
// import java.time.Duration;
// import java.time.Instant;
=======
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
// import java.io.ObjectInputFilter.Status;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.sql.Timestamp;
>>>>>>> c98faf91730db1699998a2a9b9f3871b99c96d9b
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
<<<<<<< HEAD
import java.util.Base64;
import java.nio.charset.StandardCharsets;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.nio.file.Paths;
=======
>>>>>>> c98faf91730db1699998a2a9b9f3871b99c96d9b

@Service
public class AgentService {
    private static final String SERVER_URL = "http://localhost:8080/agent";
    private static final String SERVER_URL1 = "http://localhost:8080/softwareRunning";
<<<<<<< HEAD
    // private static final String SERVER_URL =
    // "https://webhook.site/c9649b89-f4b3-4a56-8cd6-7775c4bbad56";
    @Value("${agent.api.key}")
    private String apiKey;
    private static final String API_KEY_HEADER_NAME = "x-agent-key";
=======

>>>>>>> c98faf91730db1699998a2a9b9f3871b99c96d9b

    // lấy địa chỉ mac
    public String getMacAddress() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            NetworkInterface ni = NetworkInterface.getByInetAddress(localHost);
            byte[] macBytes = ni.getHardwareAddress();

            if (macBytes == null)
                return "unknown";

            StringBuilder sb = new StringBuilder();
            for (byte b : macBytes) {
                sb.append(String.format("%02X:", b));
            }
            return sb.substring(0, sb.length() - 1); // Remove trailing colon
        } catch (Exception e) {
            return "unknown";
        }
    }

<<<<<<< HEAD
=======

>>>>>>> c98faf91730db1699998a2a9b9f3871b99c96d9b
    // lấy trạng thái tường lửa
    private FirewallStatus getFirewallStatus() {
        try {
            ProcessBuilder builder = new ProcessBuilder(
                    "powershell.exe",
                    "-Command",
                    "Get-NetFirewallProfile | Select-Object -ExpandProperty Enabled");
            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.equalsIgnoreCase("True")) {
                    return FirewallStatus.on;
                } else if (line.equalsIgnoreCase("False")) {
                    // If any profile has firewall off, consider it OFF
                    return FirewallStatus.off;
                }
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Error checking firewall status with PowerShell: " + e.getMessage());
        }

        return FirewallStatus.unknown;
    }

<<<<<<< HEAD
=======

>>>>>>> c98faf91730db1699998a2a9b9f3871b99c96d9b
    // gửi trạng thái lên server
    public void sendStatus() {
        try {
            String computerName = InetAddress.getLocalHost().getHostName();
            String ipAddress = InetAddress.getLocalHost().getHostAddress();
<<<<<<< HEAD
            String macAddress = getMacAddress();
            Status status = Status.on;
            FirewallStatus firewallStatus = getFirewallStatus();
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            int timeUse = 0; // Placeholder for useTime if needed

            AgentInfo info = new AgentInfo(computerName, ipAddress, macAddress, status, firewallStatus, timestamp,
                    timeUse);

            // 1. Tạo Headers và đính kèm API Key
            HttpHeaders headers = new HttpHeaders();
            headers.set(API_KEY_HEADER_NAME, apiKey); // Thêm Header API Key

            System.out.println("DEBUG: Sending status to " + SERVER_URL + " with Key: " + apiKey);

            // 2. Đóng gói AgentInfo (Body) và Headers
            HttpEntity<AgentInfo> request = new HttpEntity<>(info, headers);

            RestTemplate restTemplate = new RestTemplate();

            // 3. Gửi request và log response
            try {
                ResponseEntity<String> response = restTemplate.postForEntity(SERVER_URL, request, String.class);
                System.out.println("Sent agent status to server. Response: " + response.getStatusCode() + " - "
                        + response.getBody());
            } catch (HttpClientErrorException e) {
                System.err.println("Failed to send agent status: " + e.getStatusCode() + " on POST request for \""
                        + SERVER_URL + "\": " + e.getResponseBodyAsString());
            } catch (Exception e) {
                System.err.println("Failed to send agent status: " + e.getMessage());
            }
=======
            String macAddress = getMacAddress(); // ✅ Added MAC address
            Status status = Status.on;
            FirewallStatus firewallStatus = getFirewallStatus();
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            AgentInfo info = new AgentInfo(computerName, ipAddress, macAddress, status, firewallStatus, timestamp);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.postForObject(SERVER_URL, info, String.class);

            System.out.println("Sent agent status to server with MAC: " + macAddress);

>>>>>>> c98faf91730db1699998a2a9b9f3871b99c96d9b
        } catch (Exception e) {
            System.err.println("Failed to send agent status: " + e.getMessage());
        }
    }

<<<<<<< HEAD
    // lấy các chương trình đang chạy
    public void getRunningApplications() {
        List<Map<String, Object>> appList = new ArrayList<>();

        // 1. Lệnh PowerShell
        String command = "Get-Process | Where-Object { $_.MainWindowTitle } | Select-Object ProcessName, Id, MainWindowTitle, Description | ForEach-Object { \"{0}|{1}|{2}|{3}\" -f $_.ProcessName, $_.Id, $_.MainWindowTitle, $_.Description }";

        // 2. Mã hóa lệnh sang Base64 (PowerShell yêu cầu UTF-16LE)
        String encodedCommand = Base64.getEncoder().encodeToString(
                command.getBytes(StandardCharsets.UTF_16LE));

        // 3. Dùng ProcessBuilder với -EncodedCommand
        ProcessBuilder builder = new ProcessBuilder(
                "powershell.exe",
                "-EncodedCommand", // Dùng lệnh đã mã hóa
                encodedCommand);

        try {
            Process process = builder.start();

            // --- ĐỌC OUTPUT CHUẨN (InputStream) ---
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                // Tách bằng dấu |
                String[] parts = line.trim().split("\\|", 4);

                if (parts.length == 4) {
                    Map<String, Object> app = new HashMap<>();
                    app.put("name", parts[0]);
                    app.put("pid", parts[1]);
                    app.put("title", parts[2]);
                    app.put("description", parts[3]);
=======

    // lấy các chương trình đang chạy
    public void getRunningApplications() {
        List<Map<String, Object>> appList = new ArrayList<>();
        ProcessBuilder builder = new ProcessBuilder(
                "powershell.exe",
                "Get-Process | Where-Object { $_.MainWindowTitle } | Select-Object ProcessName, Id, MainWindowTitle");
        try {
            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("ProcessName")) {
                    continue; // skip header and empty lines
                }

                // Dòng ví dụ: chrome 12345 Google - Chrome
                String[] parts = line.trim().split("\\s{2,}", 3); // chia thành 3 phần: tên, PID, tiêu đề cửa sổ
                if (parts.length >= 2) {
                    Map<String, Object> app = new HashMap<>();
                    app.put("name", parts[0]);
                    app.put("pid", parts[1]);
                    app.put("title", parts.length == 3 ? parts[2] : "");
>>>>>>> c98faf91730db1699998a2a9b9f3871b99c96d9b
                    app.put("macAddress", getMacAddress());
                    appList.add(app);
                }
            }
<<<<<<< HEAD
            reader.close();

            // --- ĐỌC LỖI (ErrorStream) ---
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), "UTF-8"));
            StringBuilder errorOutput = new StringBuilder();
            String errorLine;
            while ((errorLine = errorReader.readLine()) != null) {
                errorOutput.append(errorLine).append("\n");
            }
            errorReader.close();

            int exitCode = process.waitFor();

            // Nếu có lỗi, in ra log
            if (exitCode != 0) {
                System.err.println("PowerShell process exited with code " + exitCode + ":");
                System.err.println(errorOutput.toString());
            }

            // --- Phần gửi request ---
            if (appList.isEmpty()) {
                return;
            }

            HttpHeaders headers = new HttpHeaders();
            headers.set(API_KEY_HEADER_NAME, apiKey);
            System.out.println("DEBUG: Sending running apps to " + SERVER_URL1 + " with Key: " + apiKey);
            HttpEntity<List<Map<String, Object>>> request = new HttpEntity<>(appList, headers);
            RestTemplate restTemplate = new RestTemplate();
            try {
                ResponseEntity<String> response = restTemplate.postForEntity(SERVER_URL1, request, String.class);
                System.out.println("Sent running applications to server. Response: " + response.getStatusCode() + " - "
                        + response.getBody());
            } catch (HttpClientErrorException e) {
                System.err.println("Failed to send running applications: " + e.getStatusCode()
                        + " on POST request for \"" + SERVER_URL1 + "\": " + e.getResponseBodyAsString());
            } catch (Exception e) {
                System.err.println("Failed to send running applications: " + e.getMessage());
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();

            Thread.currentThread().interrupt();
        }
    }

}
=======

            // Gửi danh sách ứng dụng đang chạy GUI lên server
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.postForObject(SERVER_URL1, appList, String.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}


>>>>>>> c98faf91730db1699998a2a9b9f3871b99c96d9b

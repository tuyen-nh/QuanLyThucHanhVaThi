package com.example.agent.Service;

import com.example.agent.Model.AgentInfo;
import com.example.agent.Model.AgentInfo.Status;
import com.example.agent.Model.AgentInfo.FirewallStatus;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
// import java.io.ObjectInputFilter.Status;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AgentService {
    private static final String SERVER_URL = "http://localhost:8080/agent";
    private static final String SERVER_URL1 = "http://localhost:8080/softwareRunning";


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


    // gửi trạng thái lên server
    public void sendStatus() {
        try {
            String computerName = InetAddress.getLocalHost().getHostName();
            String ipAddress = InetAddress.getLocalHost().getHostAddress();
            String macAddress = getMacAddress(); // ✅ Added MAC address
            Status status = Status.on;
            FirewallStatus firewallStatus = getFirewallStatus();
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            AgentInfo info = new AgentInfo(computerName, ipAddress, macAddress, status, firewallStatus, timestamp);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.postForObject(SERVER_URL, info, String.class);

            System.out.println("Sent agent status to server with MAC: " + macAddress);

        } catch (Exception e) {
            System.err.println("Failed to send agent status: " + e.getMessage());
        }
    }


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
                    app.put("macAddress", getMacAddress());
                    appList.add(app);
                }
            }

            // Gửi danh sách ứng dụng đang chạy GUI lên server
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.postForObject(SERVER_URL1, appList, String.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}



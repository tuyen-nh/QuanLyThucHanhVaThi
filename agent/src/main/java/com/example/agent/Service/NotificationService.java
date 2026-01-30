package com.example.agent.Service;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificationService {
    @Value("${agent.api.key}")
    private String apiKey;
    private static final String API_KEY_HEADER_NAME = "x-agent-key";
    private static final String SERVER_URLAI = "http://localhost:8080/AI/notification";
    // private static final String SERVER_URLAI =
    // "https://webhook.site/c9649b89-f4b3-4a56-8cd6-7775c4bbad56";

    public void sendNotification(String message, String filePath, String fileName, String detectionTime) {
        List<Map<String, Object>> AInotification = new ArrayList<>();
        Map<String, Object> notification = new HashMap<>();
        notification.put("message", message);
        notification.put("macAddress", getMacAddress());
        notification.put("filePath", filePath);
        notification.put("fileName", fileName);
        notification.put("detectionTime", detectionTime);
        AInotification.add(notification);
        HttpHeaders headers = new HttpHeaders();
        headers.set(API_KEY_HEADER_NAME, apiKey);
        System.out.println("DEBUG: Sending notification to " + SERVER_URLAI + " with Key: " + apiKey);
        HttpEntity<List<Map<String, Object>>> request = new HttpEntity<>(AInotification, headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(SERVER_URLAI, request, String.class);
            System.out.println("Sent notification to server. Response: " + response.getStatusCode() + " - "
                    + response.getBody());
        } catch (HttpClientErrorException e) {
            System.err.println("Failed to send notification: " + e.getStatusCode()
                    + " on POST request for \"" + SERVER_URLAI + "\": " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.err.println("Failed to send notification: " + e.getMessage());
        }

        // System.out.println("NOTIFICATION: " + message);
    }

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
}

package com.example.demo.Controller;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.Model.AiNotification;
import com.example.demo.Model.Computer;
import com.example.demo.Repository.AIRepo;
import com.example.demo.Repository.ComputerRepository;

@RestController
@RequestMapping("/AI")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:8085" })
public class AiController {

    @Autowired
    private AIRepo aiRepo;

    @Autowired
    private ComputerRepository computerRepository;

    @PostMapping("/notification")
    public ResponseEntity<String> receiveNotification(@RequestBody List<Map<String, Object>> data) {
        System.out.println("Received AI notification data");

        if (data == null || data.isEmpty()) {
            return ResponseEntity.badRequest().body("Data is empty");
        }

        try {
            for (Map<String, Object> notificationData : data) {
                String macAddress = (String) notificationData.get("macAddress");
                String filePath = (String) notificationData.get("filePath");
                String fileName = (String) notificationData.get("fileName");
                String detectionTimeStr = (String) notificationData.get("detectionTime");
                String message = (String) notificationData.get("message");

                Optional<Computer> computerOpt = computerRepository.findByMacAddress(macAddress);
                System.out.println(message);
                if (computerOpt.isPresent()) {
                    AiNotification notification = new AiNotification();
                    notification.setComputer(computerOpt.get());
                    notification.setFilePath(filePath);
                    notification.setFileName(fileName);

                    // Xử lý thời gian detect
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        LocalDateTime localDateTime = LocalDateTime.parse(detectionTimeStr, formatter);
                        notification.setDetectTime(Timestamp.valueOf(localDateTime));
                    } catch (Exception e) {
                        System.err.println("Cannot parse date: " + detectionTimeStr + " -> Using current time");
                        notification.setDetectTime(new Timestamp(System.currentTimeMillis()));
                    }

                    aiRepo.save(notification);
                } else {
                    System.err.println("Computer not found for MAC in AIdetector: " + macAddress);
                }
            }
            return ResponseEntity.ok("Received and processed notification");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error processing: " + e.getMessage());
        }
    }

    @GetMapping("/notifications")
    public List<AiNotification> getAllNotifications() {
        return aiRepo.findAll();
    }
}

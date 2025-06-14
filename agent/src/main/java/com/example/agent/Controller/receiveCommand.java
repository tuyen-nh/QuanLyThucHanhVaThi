package com.example.agent.Controller;

import com.example.agent.Service.Command;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class receiveCommand {
    @Autowired
    private Command commandService;

    public receiveCommand(Command commandService) {
        this.commandService = commandService;
    }

    @PostMapping("/commands")
    public String receiveCommand(@RequestBody Map<String, String> payload) {
        String softwareUrl = payload.get("url");

        if (softwareUrl == null || softwareUrl.isEmpty()) {
            return "URL phần mềm không hợp lệ";
        }

        // Gọi hàm đã có sẵn để tải và cài đặt (chạy song song)
        new Thread(() -> commandService.downloadAndInstall(softwareUrl)).start();

        return "Đã nhận lệnh cài đặt, đang xử lý...";
    }



    // bật tắt tường lửa
    @PostMapping("/firewall")
    public ResponseEntity<String> controlFirewall(@RequestBody String command) {
//        String command = body.get("command");

        try {
            if ("on".equalsIgnoreCase(command)) {
                Runtime.getRuntime().exec("netsh advfirewall set allprofiles state on");
                return ResponseEntity.ok("Firewall turned ON");
            } else if ("off".equalsIgnoreCase(command)) {
                Runtime.getRuntime().exec("netsh advfirewall set allprofiles state off");
                return ResponseEntity.ok("Firewall turned OFF");
            } else {
                return ResponseEntity.badRequest().body("Unknown command: " + command);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}

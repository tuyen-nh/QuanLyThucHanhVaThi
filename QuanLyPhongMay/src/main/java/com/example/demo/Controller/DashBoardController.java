package com.example.demo.Controller;

import com.example.demo.Model.Computer;
import com.example.demo.Model.Computer.Status;
import com.example.demo.Model.ComputerSoftwareStatus;
import com.example.demo.Model.Software;
import com.example.demo.Model.command;
import com.example.demo.Repository.CommandRepository;
import com.example.demo.Repository.ComputerRepository;
import com.example.demo.Repository.ComputerSoftwareStatusRepository;
import com.example.demo.Repository.SoftwareRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:8085" })
public class DashBoardController {
    public String nameCommandCode = "download";
    private final ComputerRepository computerRepository;
    private final ComputerSoftwareStatusRepository computerSoftwareStatus;
    private final SoftwareRepository softwareRepository;
    private final CommandRepository commandRepository;

    public DashBoardController(ComputerRepository computerRepository,
            ComputerSoftwareStatusRepository computerSoftwareStatus, SoftwareRepository softwareRepository,
            CommandRepository commandRepository) {
        this.computerRepository = computerRepository;
        this.computerSoftwareStatus = computerSoftwareStatus;
        this.softwareRepository = softwareRepository;
        this.commandRepository = commandRepository;
    }

    @Autowired

    @GetMapping("/info")
    public List<Computer> getComputerInfo() {
        return computerRepository.findAll();
    }

    // nhận thông tin máy tính ở lần đầu giám sát
    @PostMapping("/agent")
    public String dataAgent(@RequestBody Computer computer) {
        System.out.println("Receiving agent data...");
        Optional<Computer> existingComputer = computerRepository.findByMacAddress(computer.getMacAddress());
        if (existingComputer.isPresent()) {
            Computer updatedComputer = existingComputer.get();
            updatedComputer.setIpAddress(computer.getIpAddress());
            updatedComputer.setStatus(computer.getStatus());
            updatedComputer.setStatusFirewall(computer.getStatusFirewall());
            updatedComputer.setTimestamp(computer.getTimestamp());
            computerRepository.save(updatedComputer);
        } else {
            computerRepository.save(computer);
        }

        return "Data received successfully";
    }


    @PostMapping("/softwareRunning")
public ResponseEntity<String> receiveData(@RequestBody List<Map<String, Object>> data) {
    System.out.println("i received date from agent");
    //ghi in ra toàn bộ dữ liệu data ở đây
    for (Map<String, Object> item : data) {
        System.out.println(item);
    }

    if (data == null || data.size() <= 1) {
        return ResponseEntity.badRequest().body("the data is not sensible");
    }

    // Giả sử phần tử đầu là rỗng thì bỏ qua, lấy MAC từ phần tử thứ 1
    String macAddress = (String) data.get(1).get("macAddress");

    Optional<Computer> optionalComputer = computerRepository.findByMacAddress(macAddress);
    if (optionalComputer.isEmpty()) {
        return ResponseEntity.badRequest().body("Không tìm thấy máy tính với địa chỉ MAC: " + macAddress);
    }

    Computer computer = optionalComputer.get();

    // Lấy danh sách phần mềm đang chạy
    List<String> runningSoftwares = new ArrayList<>();
    for (int i = 1; i < data.size(); i++) {
        String name = (String) data.get(i).get("name");
        if (name != null && !name.isBlank()) {
            runningSoftwares.add(name);
        }
    }

    // Cập nhật trạng thái các phần mềm không còn chạy
    List<ComputerSoftwareStatus> existingStatuses = computerSoftwareStatus.findByComputer(computer);
    for (ComputerSoftwareStatus status : existingStatuses) {
        if (!runningSoftwares.contains(status.getSoftware().getNameSoftware())) {
            status.setStatus(ComputerSoftwareStatus.Status.stopped);
            computerSoftwareStatus.save(status);
        }
    }

    // Cập nhật trạng thái phần mềm đang chạy
    for (String processName : runningSoftwares) {
        Software software = softwareRepository.findByNameSoftware(processName)
    .orElseGet(() -> {
        Software newSoftware = new Software();
        newSoftware.setNameSoftware(processName);
        return softwareRepository.save(newSoftware);
    });


        Optional<ComputerSoftwareStatus> existingStatus =
                computerSoftwareStatus.findByComputerAndSoftware(computer, software);

        if (existingStatus.isEmpty()) {
            ComputerSoftwareStatus css = new ComputerSoftwareStatus();
            css.setComputer(computer);
            css.setSoftware(software);
            css.setStatus(ComputerSoftwareStatus.Status.running);
            computerSoftwareStatus.save(css);
        } else if (existingStatus.get().getStatus() != ComputerSoftwareStatus.Status.running) {
            ComputerSoftwareStatus css = existingStatus.get();
            css.setStatus(ComputerSoftwareStatus.Status.running);
            computerSoftwareStatus.save(css);
        }
    }

    return ResponseEntity.ok("Received and processed software data");
}


    // === SERVER SIDE (Spring Boot) ===
// Controller method to send software download command to Agent
@PostMapping("/sendCommandToAgent")
public ResponseEntity<String> sendCommandToAgent(@RequestBody Map<String, String> payload) {
    String macAddress = payload.get("macAddress");
    String softwareName = payload.get("softwareName");

    Optional<Computer> computerOpt = computerRepository.findByMacAddress(macAddress);
    Optional<command> commandOpt = commandRepository.findByMaLenh(softwareName);

    if (computerOpt.isPresent() && commandOpt.isPresent()) {
        String agentUrl = "http://" + computerOpt.get().getIpAddress() + ":8085/commands";
        String softwareUrl = commandOpt.get().getSoftware().getUrl(); 

        try {
            RestTemplate restTemplate = new RestTemplate();
            Map<String, String> body = new HashMap<>();
            body.put("url", softwareUrl);
            restTemplate.postForObject(agentUrl, body, String.class);
            return ResponseEntity.ok("Gửi thành công đến máy " + macAddress);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Gửi thất bại: " + e.getMessage());
        }
    } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Không tìm thấy máy hoặc phần mềm");
    }
}


    // nhân phần trăm cài đặt được của phần mềm
    private int latestProgress = 0;

    @GetMapping("/progress")
    public ResponseEntity<String> receiveProgress(@RequestParam("percent") int percent) {
        System.out.println("Tiến độ cài đặt nhận được từ agent: " + percent + "%");
        latestProgress = percent;

        // Sau này có thể cập nhật vào database, dashboard, ...
        return ResponseEntity.ok("Đã nhận tiến độ: " + percent + "%");
    }

    // API để dashboard lấy tiến độ
    @GetMapping("/progress/latest")
    public int getLatestProgress() {
        return latestProgress;
    }

    // gửi yêu cầu bật tắt tường lửa
    @PostMapping("/sendFirewallCommand")
    public ResponseEntity<String> sendFirewallCommand(@RequestBody Map<String, String> payload) {
        String macAddress = payload.get("macAddress");
        String command = payload.get("command"); // "on" hoặc "off"

        Optional<Computer> computerOpt = computerRepository.findByMacAddress(macAddress);
        if (computerOpt.isPresent()) {
            String agentUrl = "http://" + computerOpt.get().getIpAddress() + ":8085/firewall";

            try {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.postForObject(agentUrl, command, String.class);
                if (command.equalsIgnoreCase("on")) {
                    computerOpt.get().setStatusFirewall(Computer.FirewallStatus.on);
                } else if (command.equalsIgnoreCase("off")) {
                    computerOpt.get().setStatusFirewall(Computer.FirewallStatus.off);
                } else {
                    // Nếu command không hợp lệ, bạn có thể trả về lỗi hoặc xử lý tùy ý
                    return ResponseEntity.badRequest().body("Command không hợp lệ: " + command);
                }
                computerRepository.save(computerOpt.get());
                return ResponseEntity.ok("Gửi lệnh firewall thành công đến máy " + macAddress);

            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Gửi thất bại: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy máy có MAC: " + macAddress);
        }
    }

}

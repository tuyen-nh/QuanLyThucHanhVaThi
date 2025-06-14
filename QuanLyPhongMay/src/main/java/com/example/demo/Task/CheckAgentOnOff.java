package com.example.demo.Task;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.demo.Model.Computer;
import com.example.demo.Repository.ComputerRepository;

@Component
public class CheckAgentOnOff {
    @Autowired
    private ComputerRepository computerRepository;

    @Scheduled(fixedRate = 180000) // 3 phút
    public void checkInactiveComputers() {
        List<Computer> computers = computerRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (Computer computer : computers) {
            if (computer.getTimestamp() == null) continue;
            Timestamp timestamp = computer.getTimestamp();
            LocalDateTime computerTime = timestamp.toLocalDateTime();
            Duration duration = Duration.between(computerTime, now);

            // Duration duration = Duration.between(computer.getTimestamp(), now);
            if (duration.toMinutes() >= 5) {
                if (computer.getStatus() != Computer.Status.off || computer.getStatusFirewall() != Computer.FirewallStatus.off) {
                    computer.setStatus(Computer.Status.off); 

                    computer.setStatusFirewall(Computer.FirewallStatus.off);
                    computerRepository.save(computer);
                    System.out.println("Máy " + computer.getMacAddress() + " không phản hồi 3 phút -> OFF");
                }
            }
        }
    }

}

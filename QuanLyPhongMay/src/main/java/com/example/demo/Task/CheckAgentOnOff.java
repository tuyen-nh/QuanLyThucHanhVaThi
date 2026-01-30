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
<<<<<<< HEAD
import org.springframework.transaction.annotation.Transactional;
=======
>>>>>>> c98faf91730db1699998a2a9b9f3871b99c96d9b

@Component
public class CheckAgentOnOff {
    @Autowired
    private ComputerRepository computerRepository;

    @Scheduled(fixedRate = 180000) // 3 phút
<<<<<<< HEAD
    @Transactional
=======
>>>>>>> c98faf91730db1699998a2a9b9f3871b99c96d9b
    public void checkInactiveComputers() {
        List<Computer> computers = computerRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (Computer computer : computers) {
            if (computer.getTimestamp() == null) continue;
            Timestamp timestamp = computer.getTimestamp();
            LocalDateTime computerTime = timestamp.toLocalDateTime();
            Duration duration = Duration.between(computerTime, now);

<<<<<<< HEAD
            if (duration.toMinutes() >= 5) {
                if (computer.getStatus() != Computer.Status.off || computer.getStatusFirewall() != Computer.FirewallStatus.off) {
                    computer.setStatus(Computer.Status.off);
=======
            // Duration duration = Duration.between(computer.getTimestamp(), now);
            if (duration.toMinutes() >= 5) {
                if (computer.getStatus() != Computer.Status.off || computer.getStatusFirewall() != Computer.FirewallStatus.off) {
                    computer.setStatus(Computer.Status.off); 

>>>>>>> c98faf91730db1699998a2a9b9f3871b99c96d9b
                    computer.setStatusFirewall(Computer.FirewallStatus.off);
                    computerRepository.save(computer);
                    System.out.println("Máy " + computer.getMacAddress() + " không phản hồi 3 phút -> OFF");
                }
<<<<<<< HEAD
            }else {
                System.out.println("======================= sun timeUse up 3 ");
                computer.setTimeUse(computer.getTimeUse()+3);
=======
>>>>>>> c98faf91730db1699998a2a9b9f3871b99c96d9b
            }
        }
    }

}

package com.example.demo.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

<<<<<<< HEAD
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional; // Cần thiết cho các phương thức an toàn (nhưng tôi sẽ dùng toán tử ba ngôi)
=======

import java.sql.Timestamp;
import java.util.List;

>>>>>>> c98faf91730db1699998a2a9b9f3871b99c96d9b

@Entity
@Table(name = "computer")
public class Computer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "computerId")
    private int computerId;

    @Column(name = "nameComputer")
    private String nameComputer;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    // Enum nội bộ
    public enum Status {
        on, off
    }
<<<<<<< HEAD

    public enum FirewallStatus {
        on, off, unknown
    }

=======
    public enum FirewallStatus {
        on, off, unknown
    }
>>>>>>> c98faf91730db1699998a2a9b9f3871b99c96d9b
    @Column(name = "ipAddress")

    private String ipAddress;
    @Column(name = "macAddress")
    private String macAddress;
<<<<<<< HEAD

    @Enumerated(EnumType.STRING)
    @Column(name = "statusFirewall")
    private FirewallStatus statusFirewall;
=======
    @Enumerated(EnumType.STRING)
    @Column(name = "statusFirewall")
        private FirewallStatus statusFirewall;
>>>>>>> c98faf91730db1699998a2a9b9f3871b99c96d9b

    @Column(name = "timestamp")
    private Timestamp timestamp;

<<<<<<< HEAD
    // Đã sửa từ int sang Integer để cho phép NULL từ Database
    @Column(name = "timeUse")
    private int timeUse;

    @OneToMany(mappedBy = "computer", cascade = CascadeType.ALL)
    private List<ComputerSoftwareStatus> softwareStatuses;

    @OneToMany(mappedBy = "computer", cascade = CascadeType.ALL)
    private List<AiNotification> aiNotifications;

=======
//    @OneToOne(mappedBy = "computer",cascade = CascadeType.ALL)
//
//    private Firewall firewall;

    @OneToMany(mappedBy = "computer",cascade = CascadeType.ALL)
    private List<ComputerSoftwareStatus> softwareStatuses;

>>>>>>> c98faf91730db1699998a2a9b9f3871b99c96d9b
    public String getNameComputer() {
        return nameComputer;
    }

    public void setNameComputer(String nameComputer) {
        this.nameComputer = nameComputer;
    }

<<<<<<< HEAD
    public Computer() {
    }

    // Constructor đã sửa để chấp nhận List<ComputerSoftwareStatus>
    public Computer(int computerId, String nameComputer, Status status, String ipAddress, String macAddress,
            List<ComputerSoftwareStatus> softwareStatuses) {
=======
//    public Firewall getFirewall() {
//        return firewall;
//    }
//
//    public void setFirewall(Firewall firewall) {
//        this.firewall = firewall;
//    }

    public Computer() {}

    public Computer(int computerId, String nameComputer, Status status, String ipAddress, String macAddress, List<ComputerSoftwareStatus> softwareStatuses) {
>>>>>>> c98faf91730db1699998a2a9b9f3871b99c96d9b
        this.computerId = computerId;
        this.nameComputer = nameComputer;
        this.status = status;
        this.ipAddress = ipAddress;
        this.macAddress = macAddress;
<<<<<<< HEAD
        this.softwareStatuses = softwareStatuses;
    }

    // Constructor đã sửa: timeUse đổi thành Integer và chấp nhận int cho đối số
    public Computer(int computerId, String nameComputer, Status status, String ipAddress, String macAddress,
            FirewallStatus statusFirewall, Timestamp timestamp, int timeUse,
            List<ComputerSoftwareStatus> softwareStatuses) {
        this.computerId = computerId;
        this.nameComputer = nameComputer;
        this.status = status;
        this.ipAddress = ipAddress;
        this.macAddress = macAddress;
        this.statusFirewall = statusFirewall;
        this.timestamp = timestamp;
        this.timeUse = timeUse; // Gán int cho Integer là an toàn
        this.softwareStatuses = softwareStatuses;
    }

    public Computer(String nameComputer, String ipAddress, String macAddress, Status status,
            FirewallStatus firewallStatus, Timestamp timestamp) {
=======
//        this.firewall = firewall;
        this.softwareStatuses = softwareStatuses;
    }

    public Computer(String nameComputer, String ipAddress,String macAddress, Status status,FirewallStatus firewallStatus, Timestamp timestamp) {
>>>>>>> c98faf91730db1699998a2a9b9f3871b99c96d9b
        this.nameComputer = nameComputer;
        this.status = status;
        this.ipAddress = ipAddress;
        this.macAddress = macAddress;
        this.statusFirewall = firewallStatus;
        this.timestamp = timestamp;
<<<<<<< HEAD
        this.timeUse = 0; // Khởi tạo timeUse an toàn khi tạo mới
    }

=======
    }


>>>>>>> c98faf91730db1699998a2a9b9f3871b99c96d9b
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

<<<<<<< HEAD
    public int getTimeUse() {
        return timeUse;
    }

    public void setTimeUse(int timeUse) {
        this.timeUse = timeUse;
    }

=======
>>>>>>> c98faf91730db1699998a2a9b9f3871b99c96d9b
    public int getComputerId() {
        return computerId;
    }

    public void setComputerId(int computerId) {
        this.computerId = computerId;
    }

    public String getNameComPuter() {
        return nameComputer;
    }

    public void setNameComPuter(String nameComPuter) {
        this.nameComputer = nameComPuter;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public List<ComputerSoftwareStatus> getSoftwareStatuses() {
        return softwareStatuses;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public void setSoftwareStatuses(List<ComputerSoftwareStatus> softwareStatuses) {
        this.softwareStatuses = softwareStatuses;
    }

    public FirewallStatus getStatusFirewall() {
        return statusFirewall;
    }

    public void setStatusFirewall(FirewallStatus statusFirewall) {
        this.statusFirewall = statusFirewall;
    }
<<<<<<< HEAD

    public List<AiNotification> getAiNotifications() {
        return aiNotifications;
    }

    public void setAiNotifications(List<AiNotification> aiNotifications) {
        this.aiNotifications = aiNotifications;
    }
=======
>>>>>>> c98faf91730db1699998a2a9b9f3871b99c96d9b
}

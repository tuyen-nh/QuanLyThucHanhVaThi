package com.example.demo.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional; // Cần thiết cho các phương thức an toàn (nhưng tôi sẽ dùng toán tử ba ngôi)

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

    public enum FirewallStatus {
        on, off, unknown
    }

    @Column(name = "ipAddress")

    private String ipAddress;
    @Column(name = "macAddress")
    private String macAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "statusFirewall")
    private FirewallStatus statusFirewall;

    @Column(name = "timestamp")
    private Timestamp timestamp;

    // Đã sửa từ int sang Integer để cho phép NULL từ Database
    @Column(name = "timeUse")
    private int timeUse;

    @OneToMany(mappedBy = "computer", cascade = CascadeType.ALL)
    private List<ComputerSoftwareStatus> softwareStatuses;

    @OneToMany(mappedBy = "computer", cascade = CascadeType.ALL)
    private List<AiNotification> aiNotifications;

    public String getNameComputer() {
        return nameComputer;
    }

    public void setNameComputer(String nameComputer) {
        this.nameComputer = nameComputer;
    }

    public Computer() {
    }

    // Constructor đã sửa để chấp nhận List<ComputerSoftwareStatus>
    public Computer(int computerId, String nameComputer, Status status, String ipAddress, String macAddress,
            List<ComputerSoftwareStatus> softwareStatuses) {
        this.computerId = computerId;
        this.nameComputer = nameComputer;
        this.status = status;
        this.ipAddress = ipAddress;
        this.macAddress = macAddress;
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
        this.nameComputer = nameComputer;
        this.status = status;
        this.ipAddress = ipAddress;
        this.macAddress = macAddress;
        this.statusFirewall = firewallStatus;
        this.timestamp = timestamp;
        this.timeUse = 0; // Khởi tạo timeUse an toàn khi tạo mới
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public int getTimeUse() {
        return timeUse;
    }

    public void setTimeUse(int timeUse) {
        this.timeUse = timeUse;
    }

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

    public List<AiNotification> getAiNotifications() {
        return aiNotifications;
    }

    public void setAiNotifications(List<AiNotification> aiNotifications) {
        this.aiNotifications = aiNotifications;
    }
}

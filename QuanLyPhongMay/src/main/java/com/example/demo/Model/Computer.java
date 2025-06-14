package com.example.demo.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;


import java.sql.Timestamp;
import java.util.List;


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

//    @OneToOne(mappedBy = "computer",cascade = CascadeType.ALL)
//
//    private Firewall firewall;

    @OneToMany(mappedBy = "computer",cascade = CascadeType.ALL)
    private List<ComputerSoftwareStatus> softwareStatuses;

    public String getNameComputer() {
        return nameComputer;
    }

    public void setNameComputer(String nameComputer) {
        this.nameComputer = nameComputer;
    }

//    public Firewall getFirewall() {
//        return firewall;
//    }
//
//    public void setFirewall(Firewall firewall) {
//        this.firewall = firewall;
//    }

    public Computer() {}

    public Computer(int computerId, String nameComputer, Status status, String ipAddress, String macAddress, List<ComputerSoftwareStatus> softwareStatuses) {
        this.computerId = computerId;
        this.nameComputer = nameComputer;
        this.status = status;
        this.ipAddress = ipAddress;
        this.macAddress = macAddress;
//        this.firewall = firewall;
        this.softwareStatuses = softwareStatuses;
    }

    public Computer(String nameComputer, String ipAddress,String macAddress, Status status,FirewallStatus firewallStatus, Timestamp timestamp) {
        this.nameComputer = nameComputer;
        this.status = status;
        this.ipAddress = ipAddress;
        this.macAddress = macAddress;
        this.statusFirewall = firewallStatus;
        this.timestamp = timestamp;
    }


    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
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
}

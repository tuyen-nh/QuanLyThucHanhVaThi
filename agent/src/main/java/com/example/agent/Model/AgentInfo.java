package com.example.agent.Model;

import java.sql.Timestamp;

public class AgentInfo {
    private String nameComputer;
    private String ipAddress;
    private String macAddress;
    private Status status;
    private FirewallStatus statusFirewall;
    private Timestamp timestamp;
    private int useTime;

    public enum Status {
        on, off
    }
    public enum FirewallStatus {
        on, off, unknown
    }

    public AgentInfo() {
    }

    public AgentInfo(String nameComputer, String ipAddress, String macAddress, Status status, FirewallStatus statusFirewall, Timestamp timestamp) {
        this.nameComputer = nameComputer;
        this.ipAddress = ipAddress;
        this.macAddress = macAddress;
        this.status = status;
        this.statusFirewall = statusFirewall;
        this.timestamp = timestamp;
    }
    public AgentInfo(String nameComputer, String ipAddress, String macAddress, Status status, FirewallStatus statusFirewall, Timestamp timestamp, int useTime) {
        this.nameComputer = nameComputer;
        this.ipAddress = ipAddress;
        this.macAddress = macAddress;
        this.status = status;
        this.statusFirewall = statusFirewall;
        this.timestamp = timestamp;
        this.useTime = useTime;
    }

    public FirewallStatus getStatusFirewall() {
        return statusFirewall;
    }

    public void setStatusFirewall(FirewallStatus statusFirewall) {
        this.statusFirewall = statusFirewall;
    }

    public String getNameComputer() {
        return nameComputer;
    }

    public void setNameComputer(String nameComputer) {
        this.nameComputer = nameComputer;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
    public int getUseTime() {
        return useTime;
    }
    public void setUseTime(int useTime) {
        this.useTime = useTime;
    }
}

//package com.example.demo.Model;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import jakarta.persistence.*;
//
//@Entity
//@Table(name = "firewall")
//public class Firewall {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "firewallId")
//    private int firewallId;
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "status", nullable = false)
//    private Status status;
//    public enum Status{
//        on,off
//    }
//
//    @Column(name = "config")
//    private String config;
//
//    @OneToOne
//    @JoinColumn(name = "computerId")
//    @JsonIgnore
//    private Computer computer;
//
//    public int getFirewallId() {
//        return firewallId;
//    }
//
//    public void setFirewallId(int firewallId) {
//        this.firewallId = firewallId;
//    }
//
//    public Status getStatus() {
//        return status;
//    }
//
//    public void setStatus(Status status) {
//        this.status = status;
//    }
//
//    public String getConfig() {
//        return config;
//    }
//
//    public void setConfig(String config) {
//        this.config = config;
//    }
//
//    public Computer getComputer() {
//        return computer;
//    }
//
//    public void setComputer(Computer computer) {
//        this.computer = computer;
//    }
//}

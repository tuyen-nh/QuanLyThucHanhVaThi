package com.example.demo.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "computersoftwarestatus")
public class ComputerSoftwareStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "computerId", nullable = false)  // Khóa ngoại tới bảng Computer
    private Computer computer;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "softwareId", nullable = false)  // Khóa ngoại tới bảng Software
    private Software software;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "timeStamp")
    private LocalDateTime timeStamp;



    public enum Status {
        running, stopped
    }
    @Transient
    public String getSoftwareName() {
        return software != null ? software.getNameSoftware() : null;
    }


    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Computer getComputer() {
        return computer;
    }

    public void setComputer(Computer computer) {
        this.computer = computer;
    }

    public Software getSoftware() {
        return software;
    }

    public void setSoftware(Software software) {
        this.software = software;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

}

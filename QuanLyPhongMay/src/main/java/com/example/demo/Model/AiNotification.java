package com.example.demo.Model;

import org.springframework.boot.context.properties.bind.Name;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "AiNotification")
public class AiNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idAiNotification;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "computerId", nullable = false) // Khóa ngoại tới bảng Computer
    private Computer computer;

    @Column(name = "filePath")
    private String filePath;

    @Column(name = "fileName")
    private String fileName;

    @Column(name = "detectTime")
    private java.sql.Timestamp detectTime;

    public int getIdAiNotification() {
        return idAiNotification;
    }

    public void setIdAiNotification(int idAiNotification) {
        this.idAiNotification = idAiNotification;
    }

    public Computer getComputer() {
        return computer;
    }

    public void setComputer(Computer computer) {
        this.computer = computer;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public java.sql.Timestamp getDetectTime() {
        return detectTime;
    }

    public void setDetectTime(java.sql.Timestamp detectTime) {
        this.detectTime = detectTime;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("macAddress")
    public String getMacAddress() {
        return computer != null ? computer.getMacAddress() : null;
    }
}

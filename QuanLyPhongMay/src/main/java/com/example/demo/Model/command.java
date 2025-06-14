package com.example.demo.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table (name = "commands")
public class command {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "malenh")
    private String maLenh;
    @OneToOne
    @JoinColumn(name = "software_id", unique = true) // UNIQUE để đảm bảo 1-1
    private SoftwareInstall software;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMaLenh() {
        return maLenh;
    }

    public void setMaLenh(String maLenh) {
        this.maLenh = maLenh;
    }

    public SoftwareInstall getSoftware() {
        return software;
    }

    public void setSoftware(SoftwareInstall software) {
        this.software = software;
    }
}

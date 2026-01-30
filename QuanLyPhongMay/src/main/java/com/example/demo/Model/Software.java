package com.example.demo.Model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "software")
public class Software {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int softwareId ;

    @Column(name = "nameSoftware")
    private String nameSoftware;

    @OneToMany(mappedBy = "software", cascade = CascadeType.ALL)
    private List<ComputerSoftwareStatus> computerStatuses;

    public Software(int softwareId, String nameSoftware) {
        this.softwareId = softwareId;
        this.nameSoftware = nameSoftware;
    }

    public Software() {
    }

    public int getSoftwareId() {
        return softwareId;
    }

    public void setSoftwareId(int softwareId) {
        this.softwareId = softwareId;
    }

    public String getNameSoftware() {
        return nameSoftware;
    }

    public void setNameSoftware(String nameSoftware) {
        this.nameSoftware = nameSoftware;
    }

    public List<ComputerSoftwareStatus> getComputerStatuses() {
        return computerStatuses;
    }

    public void setComputerStatuses(List<ComputerSoftwareStatus> computerStatuses) {
        this.computerStatuses = computerStatuses;
    }
}

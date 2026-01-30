package com.example.demo.Model;

import jakarta.persistence.*;

import java.util.Base64;

@Entity
@Table( name = "BlackList")
public class BlackList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idBlack ;

    @Column(name = "nameBlackSoftware")
    private String nameBlackSoftware;
    public BlackList(){

    }
    public BlackList(int idBlack, String nameBlackSoftware) {
        this.idBlack = idBlack;
        this.nameBlackSoftware = nameBlackSoftware;
    }

    public int getIdBlack() {
        return idBlack;
    }

    public void setIdBlack(int idBlack) {
        this.idBlack = idBlack;
    }

    public String getNameBlackSoftware() {
        return nameBlackSoftware;
    }

    public void setNameBlackSoftware(String nameBlackSoftware) {
        this.nameBlackSoftware = nameBlackSoftware;
    }
}

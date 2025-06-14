package com.example.demo.Model;

import jakarta.persistence.*;

import java.util.List;


@Entity
@Table(name = "User")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    private int userId;

    @Column(name = "userName")
    private String userName;
    @Column(name = "passwordHash")
    private String password;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Login> logs;

    // client sẽ sử dụng constructor mặc định để tạo 1 đối tượng mới khi server nhận dữ liệu

    public enum Role{
        admin,
        user
    }

    public List<Login> getLogs() {
        return logs;
    }

    public void setLogs(List<Login> logs) {
        this.logs = logs;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String passwordHash) {
        this.password = passwordHash;
    }

}

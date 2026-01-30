package com.example.demo.Controller;

import com.example.demo.Model.Computer;
import com.example.demo.Model.User;
import com.example.demo.Repository.ComputerRepository;

import com.example.demo.Repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RestController

public class Logincontroller {
    private List<String> todos = new ArrayList<>();
    private final ComputerRepository computerRepository;
    private final UserRepository userRepository;
    @Autowired
    private UserService userService;

    public Logincontroller(ComputerRepository computerRepository, UserRepository userRepository) {
        this.computerRepository = computerRepository;
        this.userRepository = userRepository;
        todos.add("tuyen1");
        todos.add("tuyen2");
        todos.add("tuyen3");
        todos.add("tuyen4");
    }

    @GetMapping("/todos")
    public List<Computer> getAll() {
        return computerRepository.findAll();
    }

    @PostMapping("/apt/login")
    public String login(@RequestBody User loginRequest) {

        return userService.verify(loginRequest);
    }
}

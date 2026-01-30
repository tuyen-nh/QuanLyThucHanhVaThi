package com.example.demo.Controller;

import com.example.demo.Model.Computer;
import com.example.demo.Model.User;
import com.example.demo.Repository.ComputerRepository;

import com.example.demo.Repository.UserRepository;
<<<<<<< HEAD
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
=======
>>>>>>> c98faf91730db1699998a2a9b9f3871b99c96d9b
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
<<<<<<< HEAD
    @Autowired
    private UserService userService;
=======
>>>>>>> c98faf91730db1699998a2a9b9f3871b99c96d9b

    public Logincontroller(ComputerRepository computerRepository, UserRepository userRepository) {
        this.computerRepository = computerRepository;
        this.userRepository = userRepository;
        todos.add("tuyen1");
        todos.add("tuyen2");
        todos.add("tuyen3");
        todos.add("tuyen4");
    }

<<<<<<< HEAD
=======

>>>>>>> c98faf91730db1699998a2a9b9f3871b99c96d9b
    @GetMapping("/todos")
    public List<Computer> getAll() {
        return computerRepository.findAll();
    }
<<<<<<< HEAD

    @PostMapping("/apt/login")
    public String login(@RequestBody User loginRequest) {

        return userService.verify(loginRequest);
=======
    @PostMapping("/apt/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        System.out.println(loginRequest.getUserName());
        System.out.println(loginRequest.getPassword());
        Optional<User> user = userRepository.findByUserName(loginRequest.getUserName());
        System.out.println(user.get().getPassword());
        System.out.println(user.get().getUserName());

        if (user.isPresent()) {
            User user1 = user.get();
            if (user1.getPassword().equals(loginRequest.getPassword())) {
                return ResponseEntity.ok("Đăng nhập thành công!");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sai tài khoản hoặc mật khẩu!");
>>>>>>> c98faf91730db1699998a2a9b9f3871b99c96d9b
    }
}

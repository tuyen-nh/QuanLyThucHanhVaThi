package com.example.demo.Controller;

import com.example.demo.Model.Computer;
import com.example.demo.Model.RefreshToken;
import com.example.demo.Model.User;
import com.example.demo.Repository.ComputerRepository;

import com.example.demo.Repository.UserRepository;
import com.example.demo.service.RefreshTokenService;
import com.example.demo.service.UserService;
import java.util.HashMap;
import java.util.Map;
import com.example.demo.service.JwtService;
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
    // private List<String> todos = new ArrayList<>();
    private final ComputerRepository computerRepository;
    private final UserRepository userRepository;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;

    public Logincontroller(ComputerRepository computerRepository, UserRepository userRepository) {
        this.computerRepository = computerRepository;
        this.userRepository = userRepository;
    }

>>>>>>> c98faf91730db1699998a2a9b9f3871b99c96d9b
    @GetMapping("/todos")
    public List<Computer> getAll() {
        return computerRepository.findAll();
    }

    @PostMapping("/apt/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        return userService.verify(loginRequest);
    }

    @PostMapping("/apt/refresh-token")
    public ResponseEntity<?> refreshtoken(@RequestBody Map<String, String> request) {
        String requestRefreshToken = request.get("refreshToken");

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    // Generate a new Access Token (JWT)
                    String token = jwtService.generateToken(user.getUserId());
                    Map<String, String> response = new HashMap<>();
                    response.put("accessToken", token);
                    response.put("refreshToken", requestRefreshToken);
                    return ResponseEntity.ok(response);
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }
    @PostMapping("/apt/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> request) {
        String requestRefreshToken = request.get("refreshToken");
        refreshTokenService.findByToken(requestRefreshToken)
                .ifPresent(refreshTokenService::delete);
        return ResponseEntity.ok("Logout successful");
    }
}

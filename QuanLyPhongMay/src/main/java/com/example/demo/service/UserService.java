package com.example.demo.service;

import com.example.demo.Model.User;
import com.example.demo.Model.UserPrinciple;
import com.example.demo.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.Map;
import java.util.HashMap;
import com.example.demo.Model.RefreshToken;

@Service
public class UserService {
    @Autowired
    private UserRepository repo;
    @Autowired
    private JwtService jwt;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    public AuthenticationManager authManager;

    public ResponseEntity<?> verify(User user) {
        Authentication authentication = authManager
                .authenticate(new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword()));
        if (authentication.isAuthenticated()) {
            UserPrinciple principal = (UserPrinciple) authentication
                    .getPrincipal();
            String accessToken = jwt.generateToken(principal.getUserId());
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(principal.getUserId());

            Map<String, String> response = new HashMap<>();
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken.getToken());
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("fall");
    }

}

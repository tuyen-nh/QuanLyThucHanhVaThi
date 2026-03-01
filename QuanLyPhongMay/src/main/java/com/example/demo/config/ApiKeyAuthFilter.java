package com.example.demo.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

public class ApiKeyAuthFilter extends OncePerRequestFilter {

    // Khóa bí mật được INJECT từ SecurityConfig
    private final String agentApiKey;
    private static final String API_KEY_HEADER = "x-agent-key";

    // SỬA: Thêm Constructor để nhận API Key được truyền từ SecurityConfig
    public ApiKeyAuthFilter(String agentApiKey) {
        this.agentApiKey = agentApiKey;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String apiKey = request.getHeader(API_KEY_HEADER);
        String path = request.getRequestURI();

        System.out.println("Processing Request: " + path);
        System.out.println("   - Received Key: " + (apiKey != null ? apiKey : "null"));
        System.out.println("   - Expected Key: " + this.agentApiKey);

        // 1. Kiểm tra xem request có chứa API Key không
        // SỬ DỤNG biến 'agentApiKey' đã được khởi tạo qua constructor
        if (apiKey != null && apiKey.equals(this.agentApiKey)) {
            System.out.println("   -> Match! Authenticating as AgentUser.");

            // 2. Nếu Key khớp, tạo đối tượng xác thực
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    "AgentUser", // Tên người dùng ảo
                    null,
                    AuthorityUtils.createAuthorityList("ROLE_AGENT", "ROLE_USER") // Thêm ROLE_USER nếu cần
            );

            // 3. Đặt xác thực vào SecurityContextHolder
            SecurityContextHolder.getContext().setAuthentication(auth);

            // Vì Agent đã được xác thực, KHÔNG CẦN chạy JWT Filter nữa.
            filterChain.doFilter(request, response);
            return;
        }

        System.out.println("   -> No Match. Continuing chain...");
        // Nếu không có API Key, chuyển sang bộ lọc tiếp theo (JWT Filter)
        filterChain.doFilter(request, response);
    }
}
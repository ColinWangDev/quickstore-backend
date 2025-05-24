package com.quickstore.controller;

import com.quickstore.dto.LoginRequest;
import com.quickstore.dto.LoginResponse;
import com.quickstore.dto.RegisterRequest;
import com.quickstore.model.User;
import com.quickstore.security.JwtTokenProvider;
import com.quickstore.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        logger.info("Attempting to register new user: {}", registerRequest.getUsername());

        // 检查用户名是否已存在
        if (userService.findByUsername(registerRequest.getUsername()) != null) {
            logger.warn("Registration failed: Username {} already exists", registerRequest.getUsername());
            return ResponseEntity.badRequest().body("Username already exists");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFullName(registerRequest.getFullName());
        user.setRole(registerRequest.getRole().toLowerCase()); // 确保角色是小写的

        // 保存用户
        userService.save(user);
        logger.info("User registered successfully: {}", user.getUsername());

        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Attempting login for user: {}", loginRequest.getUsername());
        
        User user = userService.findByUsername(loginRequest.getUsername());
        
        if (user != null && passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            logger.info("Login successful for user: {}", user.getUsername());
            
            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPasswordHash(),
                    Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase()))
            );
            
            String token = tokenProvider.generateToken(userDetails);
            return ResponseEntity.ok(new LoginResponse(token, user.getUsername(), user.getRole()));
        }
        
        logger.warn("Login failed for user: {}", loginRequest.getUsername());
        return ResponseEntity.badRequest().body(null);
    }
} 
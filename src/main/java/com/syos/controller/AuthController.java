package com.syos.controller;

import com.syos.entity.User;
import com.syos.service.UserService;
import com.syos.domain.enums.UserType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.mindrot.jbcrypt.BCrypt;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    
    public AuthController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username and password are required"));
        }

        try {
            User user = userService.findByUsername(username.toLowerCase()).orElse(null);

            if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("user", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "userType", user.getUserType().name()
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Login failed: " + e.getMessage()));
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> userData) {
        String username = userData.get("username");
        String password = userData.get("password");
        String userTypeStr = userData.get("userType");

        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty() ||
            userTypeStr == null || userTypeStr.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username, password, and userType are required"));
        }

        try {
            if (userService.findByUsername(username.toLowerCase()).isPresent()) {
                return ResponseEntity.status(400).body(Map.of("error", "Username already exists"));
            }

            UserType userType;
            try {
                userType = UserType.valueOf(userTypeStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid user type. Use ADMIN, STAFF, or CUSTOMER"));
            }

            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            User user = new User(username, hashedPassword, userType);
            userService.save(user);

            return ResponseEntity.ok(Map.of("message", "Registration successful", "userId", user.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Registration failed: " + e.getMessage()));
        }
    }
    
    @GetMapping("/logout")
    public ResponseEntity<?> logout() {
        // In a stateless REST API, logout is typically handled by the client
        // by removing the token from storage
        return ResponseEntity.ok(Map.of("message", "Logout successful"));
    }
}
package com.academic.controller;

import com.academic.config.JwtUtil;
import com.academic.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(originPatterns = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody Map<String, String> body) {

        String email = body.get("email");
        String password = body.get("password");
        String role = body.get("role");

        Map<String, Object> result =
                authService.login(email, password, role);

        if ((Boolean) result.get("success")) {
            String token = jwtUtil.generateToken(
                    Long.valueOf(
                            result.get("id").toString()),
                    email,
                    role
            );
            result.put("token", token);
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.status(401)
                .body(result);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
            @RequestBody Map<String, Object> body) {

        Map<String, Object> result =
                authService.register(
                        (String) body.get("name"),
                        (String) body.get("email"),
                        (String) body.get("password"),
                        (String) body.get("registerNumber"),
                        (String) body.get("department"),
                        body.get("semester") != null ?
                                Integer.valueOf(
                                        body.get("semester")
                                                .toString()) : 1
                );

        return (Boolean) result.get("success") ?
                ResponseEntity.ok(result) :
                ResponseEntity.badRequest().body(result);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(
            @RequestHeader(value = "Authorization",
                    required = false)
            String authHeader) {

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Logged out successfully"));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(
            @RequestHeader(value = "Authorization",
                    required = false)
            String authHeader) {

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(
                    Map.of("success", false,
                            "message", "Not logged in"));
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body(
                    Map.of("success", false,
                            "message", "Invalid token"));
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "userId", jwtUtil.extractUserId(token),
                "role", jwtUtil.extractRole(token),
                "email", jwtUtil.extractEmail(token)
        ));
    }
}
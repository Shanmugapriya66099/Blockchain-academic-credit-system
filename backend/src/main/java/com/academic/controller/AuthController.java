package com.academic.controller;

import com.academic.service.AuthService;
import jakarta.servlet.http.HttpSession;
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

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody Map<String, String> body,
            HttpSession session) {
        String email = body.get("email");
        String password = body.get("password");
        String role = body.get("role");
        Map<String, Object> result = authService.login(email, password, role);
        if ((Boolean) result.get("success")) {
            session.setAttribute("userId", result.get("id"));
            session.setAttribute("userRole", result.get("role"));
            session.setAttribute("userName", result.get("name"));
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.status(401).body(result);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
            @RequestBody Map<String, Object> body) {
        Map<String, Object> result = authService.register(
                (String) body.get("name"),
                (String) body.get("email"),
                (String) body.get("password"),
                (String) body.get("registerNumber"),
                (String) body.get("department"),
                (Integer) body.get("semester")
        );
        return (Boolean) result.get("success") ?
                ResponseEntity.ok(result) :
                ResponseEntity.badRequest().body(result);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Logged out successfully"));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(
                    Map.of("success", false, "message", "Not logged in"));
        }
        return ResponseEntity.ok(Map.of(
                "success", true,
                "userId", userId,
                "role", session.getAttribute("userRole"),
                "name", session.getAttribute("userName")
        ));
    }
}
package com.academic.controller;

import com.academic.service.StudentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/student")
@CrossOrigin(origins = "*")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(
            HttpSession session) {
        Long userId = getUserId(session);
        if (userId == null)
            return ResponseEntity.status(401).body(
                    Map.of("success", false,
                            "message", "Please login first"));
        return ResponseEntity.ok(
                studentService.getProfile(userId));
    }

    @GetMapping("/credits")
    public ResponseEntity<Map<String, Object>> getMyCredits(
            HttpSession session) {
        Long userId = getUserId(session);
        if (userId == null)
            return ResponseEntity.status(401).body(
                    Map.of("success", false,
                            "message", "Please login first"));
        return ResponseEntity.ok(
                studentService.getMyCredits(userId));
    }

    @GetMapping("/verify/{hash}")
    public ResponseEntity<Map<String, Object>> verifyHash(
            @PathVariable String hash) {
        Map<String, Object> result =
                studentService.verifyHash(hash);
        return (Boolean) result.get("success") ?
                ResponseEntity.ok(result) :
                ResponseEntity.badRequest().body(result);
    }

    private Long getUserId(HttpSession session) {
        Object id = session.getAttribute("userId");
        return id != null ?
                Long.valueOf(id.toString()) : null;
    }
}
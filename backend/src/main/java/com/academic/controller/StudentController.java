package com.academic.controller;

import com.academic.service.StudentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/student")
@CrossOrigin(originPatterns = "*")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/credits")
    public ResponseEntity<Map<String, Object>> getMyCredits(
            @RequestParam(required = false) Long userId,
            HttpSession session) {
        Long id = userId;
        if (id == null) {
            Object sessionId = session.getAttribute("userId");
            if (sessionId != null) id = Long.valueOf(sessionId.toString());
        }
        if (id == null) {
            return ResponseEntity.status(401).body(
                    Map.of("success", false, "message", "Please login first"));
        }
        return ResponseEntity.ok(studentService.getMyCredits(id));
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(
            @RequestParam(required = false) Long userId,
            HttpSession session) {
        Long id = userId;
        if (id == null) {
            Object sessionId = session.getAttribute("userId");
            if (sessionId != null) id = Long.valueOf(sessionId.toString());
        }
        if (id == null) {
            return ResponseEntity.status(401).body(
                    Map.of("success", false, "message", "Please login first"));
        }
        return ResponseEntity.ok(studentService.getProfile(id));
    }

    @GetMapping("/verify/{hash}")
    public ResponseEntity<Map<String, Object>> verifyHash(@PathVariable String hash) {
        Map<String, Object> result = studentService.verifyHash(hash);
        return result.get("success").equals(true) ?
                ResponseEntity.ok(result) : ResponseEntity.badRequest().body(result);
    }
}
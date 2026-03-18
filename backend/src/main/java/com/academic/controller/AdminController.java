package com.academic.controller;

import com.academic.service.AdminService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(originPatterns = "*")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @PostMapping("/students")
    public ResponseEntity<Map<String, Object>> addStudent(
            @RequestBody Map<String, Object> body) {
        Map<String, Object> result = adminService.addStudent(
                (String) body.get("name"),
                (String) body.get("email"),
                (String) body.get("password"),
                (String) body.get("registerNumber"),
                (String) body.get("department"),
                body.get("semester") != null ? Integer.valueOf(body.get("semester").toString()) : 1
        );
        return result.get("success").equals(true) ?
                ResponseEntity.ok(result) : ResponseEntity.badRequest().body(result);
    }

    @GetMapping("/students")
    public ResponseEntity<List<Map<String, Object>>> getAllStudents() {
        return ResponseEntity.ok(adminService.getAllStudents());
    }

    @PostMapping("/courses")
    public ResponseEntity<Map<String, Object>> addCourse(
            @RequestBody Map<String, Object> body) {
        Map<String, Object> result = adminService.addCourse(
                (String) body.get("courseCode"),
                (String) body.get("courseName"),
                body.get("maxCredits") != null ? Integer.valueOf(body.get("maxCredits").toString()) : 4,
                (String) body.get("department")
        );
        return result.get("success").equals(true) ?
                ResponseEntity.ok(result) : ResponseEntity.badRequest().body(result);
    }

    @GetMapping("/courses")
    public ResponseEntity<?> getAllCourses() {
        return ResponseEntity.ok(adminService.getAllCourses());
    }

    @PostMapping("/credits/issue")
    public ResponseEntity<Map<String, Object>> issueCredit(
            @RequestBody Map<String, Object> body) {
        Map<String, Object> result = adminService.issueCredit(
                Long.valueOf(body.get("studentId").toString()),
                Long.valueOf(body.get("courseId").toString()),
                (String) body.get("grade"),
                body.get("creditsEarned") != null ? Integer.valueOf(body.get("creditsEarned").toString()) : 4,
                (String) body.get("transactionHash"),
                1L
        );
        return result.get("success").equals(true) ?
                ResponseEntity.ok(result) : ResponseEntity.badRequest().body(result);
    }

    @PutMapping("/credits/hash")
    public ResponseEntity<Map<String, Object>> updateHash(
            @RequestBody Map<String, Object> body) {
        Map<String, Object> result = adminService.updateTransactionHash(
                Long.valueOf(body.get("creditId").toString()),
                (String) body.get("transactionHash")
        );
        return result.get("success").equals(true) ?
                ResponseEntity.ok(result) : ResponseEntity.badRequest().body(result);
    }

    @GetMapping("/credits")
    public ResponseEntity<List<Map<String, Object>>> getAllCredits() {
        return ResponseEntity.ok(adminService.getAllIssuedCredits());
    }
}

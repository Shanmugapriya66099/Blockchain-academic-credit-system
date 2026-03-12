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
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(
            HttpSession session) {
        if (!isAdmin(session)) return unauthorized();
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @PostMapping("/students")
    public ResponseEntity<Map<String, Object>> addStudent(
            @RequestBody Map<String, Object> body,
            HttpSession session) {
        if (!isAdmin(session)) return unauthorized();
        Map<String, Object> result = adminService.addStudent(
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

    @GetMapping("/students")
    public ResponseEntity<List<Map<String, Object>>> getAllStudents(
            HttpSession session) {
        if (!isAdmin(session))
            return ResponseEntity.status(403).build();
        return ResponseEntity.ok(adminService.getAllStudents());
    }

    @PostMapping("/courses")
    public ResponseEntity<Map<String, Object>> addCourse(
            @RequestBody Map<String, Object> body,
            HttpSession session) {
        if (!isAdmin(session)) return unauthorized();
        Map<String, Object> result = adminService.addCourse(
                (String) body.get("courseCode"),
                (String) body.get("courseName"),
                (Integer) body.get("maxCredits"),
                (String) body.get("department")
        );
        return (Boolean) result.get("success") ?
                ResponseEntity.ok(result) :
                ResponseEntity.badRequest().body(result);
    }

    @GetMapping("/courses")
    public ResponseEntity<?> getAllCourses(HttpSession session) {
        if (!isAdmin(session))
            return ResponseEntity.status(403).build();
        return ResponseEntity.ok(adminService.getAllCourses());
    }

    @PostMapping("/credits/issue")
    public ResponseEntity<Map<String, Object>> issueCredit(
            @RequestBody Map<String, Object> body,
            HttpSession session) {
        if (!isAdmin(session)) return unauthorized();
        Long adminId = (Long) session.getAttribute("userId");
        Map<String, Object> result = adminService.issueCredit(
                Long.valueOf(body.get("studentId").toString()),
                Long.valueOf(body.get("courseId").toString()),
                (String) body.get("grade"),
                (Integer) body.get("creditsEarned"),
                (String) body.get("transactionHash"),
                adminId
        );
        return (Boolean) result.get("success") ?
                ResponseEntity.ok(result) :
                ResponseEntity.badRequest().body(result);
    }

    @PutMapping("/credits/hash")
    public ResponseEntity<Map<String, Object>> updateHash(
            @RequestBody Map<String, Object> body,
            HttpSession session) {
        if (!isAdmin(session)) return unauthorized();
        Map<String, Object> result = adminService.updateTransactionHash(
                Long.valueOf(body.get("creditId").toString()),
                (String) body.get("transactionHash")
        );
        return (Boolean) result.get("success") ?
                ResponseEntity.ok(result) :
                ResponseEntity.badRequest().body(result);
    }

    @GetMapping("/credits")
    public ResponseEntity<List<Map<String, Object>>> getAllCredits(
            HttpSession session) {
        if (!isAdmin(session))
            return ResponseEntity.status(403).build();
        return ResponseEntity.ok(adminService.getAllIssuedCredits());
    }

    private boolean isAdmin(HttpSession session) {
        String role = (String) session.getAttribute("userRole");
        return "ADMIN".equals(role);
    }

    private ResponseEntity<Map<String, Object>> unauthorized() {
        return ResponseEntity.status(403).body(
                Map.of("success", false,
                        "message", "Access denied. Admins only."));
    }
}
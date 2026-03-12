// =====================================================================
// AuthController.java
// =====================================================================
package com.academic.controller;

import com.academic.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody Map<String, String> body,
            HttpSession session) {

        String email = body.get("email");
        String password = body.get("password");
        String role = body.get("role"); // "ADMIN" or "STUDENT"

        Map<String, Object> result = authService.login(email, password, role);

        if ((Boolean) result.get("success")) {
            // Save user info in session
            session.setAttribute("userId", result.get("id"));
            session.setAttribute("userRole", result.get("role"));
            session.setAttribute("userName", result.get("name"));
            return ResponseEntity.ok(result);
        }

        return ResponseEntity.status(401).body(result);
    }

    // POST /api/auth/register
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

        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.badRequest().body(result);
    }

    // POST /api/auth/logout
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("success", true, "message", "Logged out successfully"));
    }

    // GET /api/auth/me  — check current session
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Not logged in"));
        }
        return ResponseEntity.ok(Map.of(
                "success", true,
                "userId", userId,
                "role", session.getAttribute("userRole"),
                "name", session.getAttribute("userName")
        ));
    }
}


// =====================================================================
// AdminController.java
// =====================================================================
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

    // GET /api/admin/dashboard
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(HttpSession session) {
        if (!isAdmin(session)) return unauthorized();
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    // ── STUDENTS ──────────────────────────────

    // POST /api/admin/students
    @PostMapping("/students")
    public ResponseEntity<Map<String, Object>> addStudent(
            @RequestBody Map<String, Object> body, HttpSession session) {
        if (!isAdmin(session)) return unauthorized();
        Map<String, Object> result = adminService.addStudent(
                (String) body.get("name"),
                (String) body.get("email"),
                (String) body.get("password"),
                (String) body.get("registerNumber"),
                (String) body.get("department"),
                (Integer) body.get("semester")
        );
        return result.get("success").equals(true) ?
                ResponseEntity.ok(result) : ResponseEntity.badRequest().body(result);
    }

    // GET /api/admin/students
    @GetMapping("/students")
    public ResponseEntity<List<Map<String, Object>>> getAllStudents(HttpSession session) {
        if (!isAdmin(session)) return ResponseEntity.status(403).build();
        return ResponseEntity.ok(adminService.getAllStudents());
    }

    // ── COURSES ──────────────────────────────

    // POST /api/admin/courses
    @PostMapping("/courses")
    public ResponseEntity<Map<String, Object>> addCourse(
            @RequestBody Map<String, Object> body, HttpSession session) {
        if (!isAdmin(session)) return unauthorized();
        Map<String, Object> result = adminService.addCourse(
                (String) body.get("courseCode"),
                (String) body.get("courseName"),
                (Integer) body.get("maxCredits"),
                (String) body.get("department")
        );
        return result.get("success").equals(true) ?
                ResponseEntity.ok(result) : ResponseEntity.badRequest().body(result);
    }

    // GET /api/admin/courses
    @GetMapping("/courses")
    public ResponseEntity<?> getAllCourses(HttpSession session) {
        if (!isAdmin(session)) return ResponseEntity.status(403).build();
        return ResponseEntity.ok(adminService.getAllCourses());
    }

    // ── CREDITS ──────────────────────────────

    // POST /api/admin/credits/issue
    @PostMapping("/credits/issue")
    public ResponseEntity<Map<String, Object>> issueCredit(
            @RequestBody Map<String, Object> body, HttpSession session) {
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
        return result.get("success").equals(true) ?
                ResponseEntity.ok(result) : ResponseEntity.badRequest().body(result);
    }

    // PUT /api/admin/credits/hash
    @PutMapping("/credits/hash")
    public ResponseEntity<Map<String, Object>> updateHash(
            @RequestBody Map<String, Object> body, HttpSession session) {
        if (!isAdmin(session)) return unauthorized();
        Map<String, Object> result = adminService.updateTransactionHash(
                Long.valueOf(body.get("creditId").toString()),
                (String) body.get("transactionHash")
        );
        return result.get("success").equals(true) ?
                ResponseEntity.ok(result) : ResponseEntity.badRequest().body(result);
    }

    // GET /api/admin/credits
    @GetMapping("/credits")
    public ResponseEntity<List<Map<String, Object>>> getAllCredits(HttpSession session) {
        if (!isAdmin(session)) return ResponseEntity.status(403).build();
        return ResponseEntity.ok(adminService.getAllIssuedCredits());
    }

    // ── HELPERS ──────────────────────────────
    private boolean isAdmin(HttpSession session) {
        String role = (String) session.getAttribute("userRole");
        return "ADMIN".equals(role);
    }

    private ResponseEntity<Map<String, Object>> unauthorized() {
        return ResponseEntity.status(403).body(
                Map.of("success", false, "message", "Access denied. Admins only.")
        );
    }
}


// =====================================================================
// StudentController.java
// =====================================================================
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

    // GET /api/student/profile
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(HttpSession session) {
        Long userId = getUserId(session);
        if (userId == null) return ResponseEntity.status(401).body(
                Map.of("success", false, "message", "Please login first"));
        return ResponseEntity.ok(studentService.getProfile(userId));
    }

    // GET /api/student/credits  — My results
    @GetMapping("/credits")
    public ResponseEntity<Map<String, Object>> getMyCredits(HttpSession session) {
        Long userId = getUserId(session);
        if (userId == null) return ResponseEntity.status(401).body(
                Map.of("success", false, "message", "Please login first"));
        return ResponseEntity.ok(studentService.getMyCredits(userId));
    }

    // GET /api/student/verify/{hash}  — Verify any transaction hash
    @GetMapping("/verify/{hash}")
    public ResponseEntity<Map<String, Object>> verifyHash(@PathVariable String hash) {
        Map<String, Object> result = studentService.verifyHash(hash);
        return result.get("success").equals(true) ?
                ResponseEntity.ok(result) : ResponseEntity.badRequest().body(result);
    }

    private Long getUserId(HttpSession session) {
        Object id = session.getAttribute("userId");
        return id != null ? Long.valueOf(id.toString()) : null;
    }
}
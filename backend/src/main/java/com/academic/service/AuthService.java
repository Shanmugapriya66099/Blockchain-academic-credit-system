package com.academic.service;

import com.academic.model.Student;
import com.academic.model.User;
import com.academic.repository.StudentRepository;
import com.academic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Map<String, Object> login(String email, String password, String role) {
        Map<String, Object> result = new HashMap<>();

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            result.put("success", false);
            result.put("message", "Invalid email or password");
            return result;
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            result.put("success", false);
            result.put("message", "Invalid email or password");
            return result;
        }

        if (!user.getRole().name().equals(role)) {
            result.put("success", false);
            result.put("message", "You are not registered as " + role);
            return result;
        }

        result.put("success", true);
        result.put("message", "Login successful");
        result.put("id", user.getId());
        result.put("name", user.getName());
        result.put("email", user.getEmail());
        result.put("role", user.getRole().name());

        if (user.getRole() == User.Role.STUDENT) {
            studentRepository.findByUser(user).ifPresent(student -> {
                result.put("registerNumber", student.getRegisterNumber());
                result.put("studentId", student.getId());
                result.put("department", student.getDepartment());
                result.put("semester", student.getSemester());
            });
        }

        return result;
    }

    public Map<String, Object> register(String name, String email, String password,
                                        String registerNumber, String department, Integer semester) {
        Map<String, Object> result = new HashMap<>();

        if (userRepository.existsByEmail(email)) {
            result.put("success", false);
            result.put("message", "Email already registered");
            return result;
        }

        if (studentRepository.existsByRegisterNumber(registerNumber)) {
            result.put("success", false);
            result.put("message", "Register number already exists");
            return result;
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(User.Role.STUDENT);
        User savedUser = userRepository.save(user);

        Student student = new Student();
        student.setUser(savedUser);
        student.setRegisterNumber(registerNumber);
        student.setDepartment(department);
        student.setSemester(semester);
        studentRepository.save(student);

        result.put("success", true);
        result.put("message", "Registration successful! You can now login.");
        return result;
    }
}
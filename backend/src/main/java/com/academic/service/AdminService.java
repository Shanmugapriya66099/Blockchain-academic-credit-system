package com.academic.service;

import com.academic.model.*;
import com.academic.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AdminService {

    @Autowired private UserRepository userRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private IssuedCreditRepository issuedCreditRepository;
    @Autowired private BCryptPasswordEncoder passwordEncoder;

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", studentRepository.count());
        stats.put("totalCourses", courseRepository.count());
        stats.put("totalCreditsIssued", issuedCreditRepository.count());
        stats.put("verifiedOnChain",
                issuedCreditRepository.countByBlockchainStatus(
                        IssuedCredit.BlockchainStatus.VERIFIED));
        return stats;
    }

    public Map<String, Object> addStudent(String name, String email, String password,
                                          String registerNumber, String department,
                                          Integer semester) {
        Map<String, Object> result = new HashMap<>();

        if (userRepository.existsByEmail(email)) {
            result.put("success", false);
            result.put("message", "Email already exists");
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
        result.put("message", "Student added successfully");
        return result;
    }

    public List<Map<String, Object>> getAllStudents() {
        List<Student> students = studentRepository.findAll();
        List<Map<String, Object>> list = new ArrayList<>();
        for (Student s : students) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", s.getId());
            m.put("name", s.getUser().getName());
            m.put("email", s.getUser().getEmail());
            m.put("registerNumber", s.getRegisterNumber());
            m.put("department", s.getDepartment());
            m.put("semester", s.getSemester());
            list.add(m);
        }
        return list;
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Map<String, Object> addCourse(String courseCode, String courseName,
                                         Integer maxCredits, String department) {
        Map<String, Object> result = new HashMap<>();

        if (courseRepository.existsByCourseCode(courseCode)) {
            result.put("success", false);
            result.put("message", "Course code already exists");
            return result;
        }

        Course course = new Course();
        course.setCourseCode(courseCode);
        course.setCourseName(courseName);
        course.setMaxCredits(maxCredits);
        course.setDepartment(department);
        courseRepository.save(course);

        result.put("success", true);
        result.put("message", "Course added successfully");
        return result;
    }

    public Map<String, Object> issueCredit(Long studentId, Long courseId, String grade,
                                           Integer creditsEarned, String transactionHash,
                                           Long adminId) {
        Map<String, Object> result = new HashMap<>();

        Optional<Student> studentOpt = studentRepository.findById(studentId);
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        Optional<User> adminOpt = userRepository.findById(adminId);

        if (studentOpt.isEmpty() || courseOpt.isEmpty()) {
            result.put("success", false);
            result.put("message", "Student or Course not found");
            return result;
        }

        IssuedCredit credit = new IssuedCredit();
        credit.setStudent(studentOpt.get());
        credit.setCourse(courseOpt.get());
        credit.setGrade(grade);
        credit.setCreditsEarned(creditsEarned);
        credit.setIssuedBy(adminOpt.orElse(null));

        if (transactionHash != null && !transactionHash.isEmpty()) {
            credit.setTransactionHash(transactionHash);
            credit.setBlockchainStatus(IssuedCredit.BlockchainStatus.VERIFIED);
        } else {
            credit.setBlockchainStatus(IssuedCredit.BlockchainStatus.PENDING);
        }

        IssuedCredit saved = issuedCreditRepository.save(credit);

        result.put("success", true);
        result.put("message", "Credit issued successfully");
        result.put("creditId", saved.getId());
        return result;
    }

    public Map<String, Object> updateTransactionHash(Long creditId, String transactionHash) {
        Map<String, Object> result = new HashMap<>();

        Optional<IssuedCredit> creditOpt = issuedCreditRepository.findById(creditId);
        if (creditOpt.isEmpty()) {
            result.put("success", false);
            result.put("message", "Credit record not found");
            return result;
        }

        IssuedCredit credit = creditOpt.get();
        credit.setTransactionHash(transactionHash);
        credit.setBlockchainStatus(IssuedCredit.BlockchainStatus.VERIFIED);
        issuedCreditRepository.save(credit);

        result.put("success", true);
        result.put("message", "Transaction hash updated!");
        return result;
    }

    public List<Map<String, Object>> getAllIssuedCredits() {
        List<IssuedCredit> credits = issuedCreditRepository.findAll();
        List<Map<String, Object>> list = new ArrayList<>();
        for (IssuedCredit c : credits) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", c.getId());
            m.put("studentName", c.getStudent().getUser().getName());
            m.put("registerNumber", c.getStudent().getRegisterNumber());
            m.put("courseName", c.getCourse().getCourseName());
            m.put("courseCode", c.getCourse().getCourseCode());
            m.put("grade", c.getGrade());
            m.put("creditsEarned", c.getCreditsEarned());
            m.put("transactionHash", c.getTransactionHash());
            m.put("blockchainStatus", c.getBlockchainStatus().name());
            m.put("issuedAt", c.getIssuedAt());
            list.add(m);
        }
        return list;
    }
}
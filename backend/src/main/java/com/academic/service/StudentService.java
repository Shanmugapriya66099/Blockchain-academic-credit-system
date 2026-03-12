package com.academic.service;

import com.academic.model.IssuedCredit;
import com.academic.model.Student;
import com.academic.model.User;
import com.academic.repository.IssuedCreditRepository;
import com.academic.repository.StudentRepository;
import com.academic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StudentService {

    @Autowired private StudentRepository studentRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private IssuedCreditRepository issuedCreditRepository;

    public Map<String, Object> getProfile(Long userId) {
        Map<String, Object> result = new HashMap<>();

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            result.put("success", false);
            result.put("message", "User not found");
            return result;
        }

        User user = userOpt.get();
        Optional<Student> studentOpt = studentRepository.findByUser(user);
        if (studentOpt.isEmpty()) {
            result.put("success", false);
            result.put("message", "Student not found");
            return result;
        }

        Student student = studentOpt.get();
        result.put("success", true);
        result.put("name", user.getName());
        result.put("email", user.getEmail());
        result.put("registerNumber", student.getRegisterNumber());
        result.put("department", student.getDepartment());
        result.put("semester", student.getSemester());
        return result;
    }

    public Map<String, Object> getMyCredits(Long userId) {
        Map<String, Object> result = new HashMap<>();

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            result.put("success", false);
            result.put("message", "User not found");
            return result;
        }

        Optional<Student> studentOpt = studentRepository.findByUser(userOpt.get());
        if (studentOpt.isEmpty()) {
            result.put("success", false);
            result.put("message", "Student not found");
            return result;
        }

        Student student = studentOpt.get();
        List<IssuedCredit> credits = issuedCreditRepository
                .findByStudentOrderByIssuedAtDesc(student);

        List<Map<String, Object>> creditList = new ArrayList<>();
        int totalCredits = 0;
        double totalPoints = 0;

        for (IssuedCredit c : credits) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", c.getId());
            m.put("courseName", c.getCourse().getCourseName());
            m.put("courseCode", c.getCourse().getCourseCode());
            m.put("grade", c.getGrade());
            m.put("creditsEarned", c.getCreditsEarned());
            m.put("transactionHash", c.getTransactionHash());
            m.put("blockchainStatus", c.getBlockchainStatus().name());
            m.put("issuedAt", c.getIssuedAt());
            creditList.add(m);

            totalCredits += c.getCreditsEarned();
            totalPoints += gradeToPoint(c.getGrade()) * c.getCreditsEarned();
        }

        double cgpa = totalCredits > 0 ?
                Math.round((totalPoints / totalCredits) * 10.0) / 10.0 : 0.0;

        result.put("success", true);
        result.put("credits", creditList);
        result.put("totalCredits", totalCredits);
        result.put("cgpa", cgpa);
        result.put("totalCourses", credits.size());
        return result;
    }

    public Map<String, Object> verifyHash(String transactionHash) {
        Map<String, Object> result = new HashMap<>();

        Optional<IssuedCredit> creditOpt =
                issuedCreditRepository.findByTransactionHash(transactionHash);
        if (creditOpt.isEmpty()) {
            result.put("success", false);
            result.put("message", "No record found for this transaction hash");
            return result;
        }

        IssuedCredit credit = creditOpt.get();
        result.put("success", true);
        result.put("studentName", credit.getStudent().getUser().getName());
        result.put("registerNumber", credit.getStudent().getRegisterNumber());
        result.put("courseName", credit.getCourse().getCourseName());
        result.put("courseCode", credit.getCourse().getCourseCode());
        result.put("grade", credit.getGrade());
        result.put("creditsEarned", credit.getCreditsEarned());
        result.put("transactionHash", credit.getTransactionHash());
        result.put("blockchainStatus", credit.getBlockchainStatus().name());
        result.put("issuedAt", credit.getIssuedAt());
        result.put("etherscanUrl",
                "https://sepolia.etherscan.io/tx/" + transactionHash);
        return result;
    }

    private double gradeToPoint(String grade) {
        return switch (grade.toUpperCase()) {
            case "O"  -> 10.0;
            case "A+" -> 9.0;
            case "A"  -> 8.0;
            case "B+" -> 7.0;
            case "B"  -> 6.0;
            case "C"  -> 5.0;
            default   -> 0.0;
        };
    }
}
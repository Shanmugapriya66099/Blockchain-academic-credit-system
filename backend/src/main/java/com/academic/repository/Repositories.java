// ===================== UserRepository.java =====================
package com.academic.repository;

import com.academic.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}


// ===================== StudentRepository.java =====================
package com.academic.repository;

import com.academic.model.Student;
import com.academic.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByRegisterNumber(String registerNumber);
    Optional<Student> findByUser(User user);
    boolean existsByRegisterNumber(String registerNumber);
}


// ===================== CourseRepository.java =====================
package com.academic.repository;

import com.academic.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    boolean existsByCourseCode(String courseCode);
    List<Course> findByDepartmentContaining(String department);
}


// ===================== IssuedCreditRepository.java =====================
package com.academic.repository;

import com.academic.model.IssuedCredit;
import com.academic.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IssuedCreditRepository extends JpaRepository<IssuedCredit, Long> {
    List<IssuedCredit> findByStudent(Student student);
    List<IssuedCredit> findByStudentOrderByIssuedAtDesc(Student student);
    Optional<IssuedCredit> findByTransactionHash(String transactionHash);
    long countByBlockchainStatus(IssuedCredit.BlockchainStatus status);
}
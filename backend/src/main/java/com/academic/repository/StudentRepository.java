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
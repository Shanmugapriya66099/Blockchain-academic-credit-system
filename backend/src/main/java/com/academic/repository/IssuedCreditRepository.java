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
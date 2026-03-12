package com.academic.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "issued_credits")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssuedCredit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false)
    private String grade;

    @Column(name = "credits_earned", nullable = false)
    private Integer creditsEarned;

    @Column(name = "transaction_hash")
    private String transactionHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "blockchain_status")
    private BlockchainStatus blockchainStatus = BlockchainStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "issued_by")
    private User issuedBy;

    @Column(name = "issued_at")
    private LocalDateTime issuedAt;

    @PrePersist
    public void prePersist() {
        this.issuedAt = LocalDateTime.now();
    }

    public enum BlockchainStatus {
        PENDING,
        VERIFIED
    }
}
package com.justiceserve.judgmentservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "judgments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Judgment {
    public enum JudgmentStatus {DRAFT, FINAL}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long judgmentId;
    @Column(nullable = false)
    private Long caseId;
    @Column(nullable = false)
    private Long judgeId;
    private Long citizenUserId;
    private Long lawyerUserId;
    private String caseTitle;
    @Column(columnDefinition = "TEXT")
    private String summary;
    private LocalDate date;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private JudgmentStatus status;

    @PrePersist
    public void prePersist() {
        this.date = LocalDate.now();
        if (status == null) status = JudgmentStatus.DRAFT;
    }
}
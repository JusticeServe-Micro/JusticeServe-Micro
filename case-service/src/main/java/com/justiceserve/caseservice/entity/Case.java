package com.justiceserve.caseservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cases")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Case {

    public enum CaseStatus {
        FILED, UNDER_REVIEW, ACTIVE, HEARING_SCHEDULED, JUDGMENT_PENDING, CLOSED, DISMISSED
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long caseId;

    /** References Citizen.citizenId in citizen-service — plain Long, no local table */
    @Column(nullable = false)
    private Long citizenId;

    /** References User.userId (LAWYER) in identity-service — plain Long */
    private Long lawyerId;

    /** References User.userId (JUDGE) in identity-service — plain Long */
    private Long judgeId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDate filedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private CaseStatus status;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        filedDate = LocalDate.now();
        createdAt = LocalDateTime.now();
        if (status == null) status = CaseStatus.FILED;
    }
}

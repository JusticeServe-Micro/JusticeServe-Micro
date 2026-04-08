package com.justiceserve.hearingservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "hearings")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Hearing {

    public enum HearingStatus { SCHEDULED, IN_PROGRESS, COMPLETED, ADJOURNED, CANCELLED }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hearingId;

    @Column(nullable = false)
    private Long caseId;

    @Column(nullable = false)
    private Long judgeId;

    @Column(length = 100)
    private String judgeName;

    @Column(nullable = false)
    private Long citizenUserId;

    private Long lawyerUserId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime time;

    @Column(length = 250)
    private String caseTitle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private HearingStatus status;

    @PrePersist
    public void prePersist() {
        if (status == null) status = HearingStatus.SCHEDULED;
    }
}

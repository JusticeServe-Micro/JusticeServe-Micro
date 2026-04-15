package com.justiceserve.hearingservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "proceedings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proceeding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long proceedingId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hearing_id", nullable = false)
    private Hearing hearing;
    @Column(columnDefinition = "TEXT")
    private String notes;
    private LocalDate date;
    @Column(length = 20)
    private String status;

    @PrePersist
    public void prePersist() {
        this.date = LocalDate.now();
        if (status == null) status = "IN_PROGRESS";
    }
}

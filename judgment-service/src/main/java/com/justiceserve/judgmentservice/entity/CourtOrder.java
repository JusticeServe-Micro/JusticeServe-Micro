package com.justiceserve.judgmentservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "court_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourtOrder {
    public enum OrderStatus {ACTIVE, SERVED, EXPIRED}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    @Column(nullable = false)
    private Long caseId;
    @Column(nullable = false)
    private Long judgeId;
    private Long citizenUserId;
    private Long lawyerUserId;
    @Column(columnDefinition = "TEXT")
    private String description;
    private LocalDate date;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private OrderStatus status;

    @PrePersist
    public void prePersist() {
        this.date = LocalDate.now();
        if (status == null) status = OrderStatus.ACTIVE;
    }
}
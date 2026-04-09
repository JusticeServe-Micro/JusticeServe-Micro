package com.justiceserve.citizenservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Citizen profile — stored ONLY in justice-serve-citizen database.
 * userId is a plain Long column referencing User.userId in identity-service.
 * NO @Entity User class here — no local users table created.
 * Data is fetched from identity-service via Feign when needed.
 */
@Entity
@Table(name = "citizens")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Citizen {

    public enum Status { ACTIVE, INACTIVE }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long citizenId;

    /** References User.userId in identity-service — NOT a local FK */
    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false, length = 100)
    private String name;

    private LocalDate dob;

    @Column(length = 10)
    private String gender;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(length = 200)
    private String contactInfo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Status status;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        if (status == null) status = Status.ACTIVE;
    }
}

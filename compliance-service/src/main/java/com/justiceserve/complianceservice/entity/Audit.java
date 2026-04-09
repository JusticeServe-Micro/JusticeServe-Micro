package com.justiceserve.complianceservice.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
@Entity @Table(name = "audits")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Audit {
    public enum AuditStatus { OPEN, REVIEW, CLOSED }
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long auditId;
    @Column(nullable=false) private Long officerId;
    @Column(length=200) private String scope;
    @Column(columnDefinition="TEXT") private String findings;
    private LocalDate date;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=10) private AuditStatus status;
    @PrePersist public void prePersist() { this.date=LocalDate.now(); if (status==null) status=AuditStatus.OPEN; }
}
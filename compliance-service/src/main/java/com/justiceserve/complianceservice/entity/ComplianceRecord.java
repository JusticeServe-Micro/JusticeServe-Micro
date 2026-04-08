package com.justiceserve.complianceservice.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
@Entity @Table(name = "compliance_records")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ComplianceRecord {
    public enum ComplianceType { CASE, HEARING, JUDGMENT }
    public enum ComplianceResult { COMPLIANT, NON_COMPLIANT, PENDING }
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long complianceId;
    @Column(nullable=false) private Long entityId;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=15) private ComplianceType type;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=15) private ComplianceResult result;
    private LocalDate date;
    @Column(columnDefinition="TEXT") private String notes;
    @PrePersist public void prePersist() { this.date=LocalDate.now(); if (result==null) result=ComplianceResult.PENDING; }
}
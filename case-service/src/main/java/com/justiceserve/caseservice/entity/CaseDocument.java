package com.justiceserve.caseservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "case_documents")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CaseDocument {

    public enum DocType { PETITION, EVIDENCE, ORDER, ID_PROOF, LEGAL_DOC }
    public enum VerificationStatus { PENDING, VERIFIED, REJECTED }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long documentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private Case caseEntity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DocType docType;

    @Column(nullable = false, length = 300)
    private String fileUri;

    private LocalDate uploadedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private VerificationStatus verificationStatus;

    @PrePersist
    public void prePersist() {
        uploadedDate = LocalDate.now();
        if (verificationStatus == null) verificationStatus = VerificationStatus.PENDING;
    }
}

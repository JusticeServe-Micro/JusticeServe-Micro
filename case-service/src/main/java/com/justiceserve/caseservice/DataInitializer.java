package com.justiceserve.caseservice;

import com.justiceserve.caseservice.entity.Case;
import com.justiceserve.caseservice.entity.CaseDocument;
import com.justiceserve.caseservice.repository.CaseDocumentRepository;
import com.justiceserve.caseservice.repository.CaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CaseRepository caseRepo;
    private final CaseDocumentRepository caseDocRepo;

    @Override
    public void run(String... args) {
        // Seed cases
        Case case1 = seedCase(1L, 4L, 2L, "Property Dispute Case", "Dispute over land ownership in Mumbai.", Case.CaseStatus.ACTIVE);
        Case case2 = seedCase(2L, 8L, 9L, "Criminal Assault Case", "Assault incident reported in Delhi.", Case.CaseStatus.HEARING_SCHEDULED);
        Case case3 = seedCase(3L, 10L, 2L, "Contract Breach Case", "Breach of employment contract.", Case.CaseStatus.UNDER_REVIEW);

        // Seed documents for cases
        seedCaseDocument(case1, CaseDocument.DocType.PETITION, "petition_case1.pdf", CaseDocument.VerificationStatus.VERIFIED);
        seedCaseDocument(case1, CaseDocument.DocType.EVIDENCE, "evidence_case1.jpg", CaseDocument.VerificationStatus.PENDING);
        seedCaseDocument(case2, CaseDocument.DocType.ID_PROOF, "id_proof_case2.pdf", CaseDocument.VerificationStatus.VERIFIED);
    }

    private Case seedCase(Long citizenId, Long lawyerId, Long judgeId, String title, String description, Case.CaseStatus status) {
        Case c = Case.builder()
                .citizenId(citizenId)
                .lawyerId(lawyerId)
                .judgeId(judgeId)
                .title(title)
                .description(description)
                .status(status)
                .build();
        caseRepo.save(c);
        log.info("Seeded case: {}", title);
        return c;
    }

    private void seedCaseDocument(Case caseEntity, CaseDocument.DocType docType, String fileUri, CaseDocument.VerificationStatus status) {
        CaseDocument doc = CaseDocument.builder()
                .caseEntity(caseEntity)
                .docType(docType)
                .fileUri(fileUri)
                .verificationStatus(status)
                .build();
        caseDocRepo.save(doc);
        log.info("Seeded case document: {} for case {}", fileUri, caseEntity.getTitle());
    }
}

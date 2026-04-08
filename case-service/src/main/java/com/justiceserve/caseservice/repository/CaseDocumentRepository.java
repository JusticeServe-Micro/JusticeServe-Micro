package com.justiceserve.caseservice.repository;

import com.justiceserve.caseservice.entity.CaseDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CaseDocumentRepository extends JpaRepository<CaseDocument, Long> {
    List<CaseDocument> findByCaseEntityCaseId(Long caseId);
}

package com.justiceserve.caseservice.service;

import com.justiceserve.caseservice.dto.*;
import com.justiceserve.caseservice.entity.Case;
import com.justiceserve.caseservice.entity.CaseDocument;
import java.util.List;

public interface CaseService {
    CaseResponse fileCase(CaseRequest req);
    CaseResponse getCaseById(Long id);
    List<CaseResponse> getAllCases();
    List<CaseResponse> getCasesByCitizen(Long citizenId);
    List<CaseResponse> getCasesByLawyer(Long lawyerId);
    List<CaseResponse> getCasesByStatus(Case.CaseStatus status);
    CaseResponse updateCaseStatus(Long id, Case.CaseStatus status);
    CaseResponse assignLawyer(Long caseId, Long lawyerId);
    CaseResponse removeLawyer(Long caseId);
    CaseResponse assignJudge(Long caseId, Long judgeId);
    DocumentResponse addDocument(Long caseId, DocumentRequest req);
    List<DocumentResponse> getDocuments(Long caseId);
    DocumentResponse verifyDocument(Long docId, CaseDocument.VerificationStatus status);
}

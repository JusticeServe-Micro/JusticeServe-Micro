package com.justiceserve.caseservice.service.impl;

import com.justiceserve.caseservice.dto.*;
import com.justiceserve.caseservice.entity.*;
import com.justiceserve.caseservice.exception.*;
import com.justiceserve.caseservice.feign.*;
import com.justiceserve.caseservice.repository.*;
import com.justiceserve.caseservice.service.CaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CaseServiceImpl implements CaseService {

    private final CaseRepository caseRepo;
    private final CaseDocumentRepository docRepo;
    private final CitizenFeignClient citizenClient;
    private final IdentityFeignClient identityClient;
    private final NotificationFeignClient notifClient;
    private final AuditFeignClient auditClient;

    @Override
//    @Transactional
//    public CaseResponse fileCase(CaseRequest req) {
//        log.info("Filing case citizenId={}, lawyerId={}", req.getCitizenId(), req.getLawyerId());
//
//        // Verify citizen exists in citizen-service via Feign — NOT a local table lookup
//        String citizenName = "Unknown";
//        Long citizenUserId = null;
//        try {
//            var citizen = citizenClient.getCitizenById(req.getCitizenId());
//            if (citizen == null || citizen.citizenId() == null)
//                throw new BadRequestException("Citizen not found: " + req.getCitizenId());
//            citizenName = citizen.name();
//            citizenUserId = citizen.userId();
//        } catch (BadRequestException e) { throw e; }
//        catch (Exception e) { log.warn("Could not verify citizen: {}", e.getMessage()); }
//
//        // Verify lawyer exists in identity-service via Feign — NOT a local table lookup
//        String lawyerName = null;
//        if (req.getLawyerId() != null) {
//            try {
//                var lawyer = identityClient.getUserById(req.getLawyerId());
//                if (!"LAWYER".equals(lawyer.role()))
//                    throw new BadRequestException("User is not a LAWYER: " + req.getLawyerId());
//                lawyerName = lawyer.name();
//            } catch (BadRequestException e) { throw e; }
//            catch (Exception e) { log.warn("Could not verify lawyer: {}", e.getMessage()); }
//        }
//
//        Case saved = caseRepo.save(Case.builder()
//                .citizenId(req.getCitizenId())
//                .lawyerId(req.getLawyerId())
//                .title(req.getTitle())
//                .description(req.getDescription())
//                .build());
//
//        log.info("Case #{} filed for citizenId={}", saved.getCaseId(), saved.getCitizenId());
//
//        if (citizenUserId != null) audit(citizenUserId, "CASE_FILED", "Case:" + saved.getCaseId());
//        if (lawyerName != null && req.getLawyerId() != null) {
//            final String ln = lawyerName;
//            if (citizenUserId != null)
//                notify(citizenUserId, saved.getCaseId(), "CASE",
//                        "Your case \"" + saved.getTitle() + "\" filed. Lawyer " + ln + " linked.");
//            notify(req.getLawyerId(), saved.getCaseId(), "CASE",
//                    "You are linked as lawyer for Case #" + saved.getCaseId() + ": \"" + saved.getTitle() + "\"");
//        } else if (citizenUserId != null) {
//            notify(citizenUserId, saved.getCaseId(), "CASE",
//                    "Case \"" + saved.getTitle() + "\" (Case #" + saved.getCaseId() + ") filed successfully.");
//        }
//
//        CaseResponse resp = CaseResponse.from(saved);
//        resp.setCitizenName(citizenName);
//        resp.setLawyerName(lawyerName);
//        return resp;
//    }
    @Transactional
    public CaseResponse fileCase(CaseRequest req) {
        log.info("Filing case citizenId={}, lawyerId={}", req.getCitizenId(), req.getLawyerId());

        // 1. Mandatory Citizen Verification
        var citizen = citizenClient.getCitizenById(req.getCitizenId());
        if (citizen == null || citizen.citizenId() == null) {
            throw new BadRequestException("Citizen not found: " + req.getCitizenId());
        }
        String citizenName = citizen.name();
        Long citizenUserId = citizen.userId();

        // 2. Lawyer Verification (Mandatory IF lawyerId is present)
        String lawyerName = null;
        if (req.getLawyerId() != null) {
            // We let Feign exceptions propagate or handle specific logic
            var lawyer = identityClient.getUserById(req.getLawyerId());

            if (lawyer == null) {
                throw new BadRequestException("Lawyer not found with ID: " + req.getLawyerId());
            }
            if (!"LAWYER".equals(lawyer.role())) {
                throw new BadRequestException("User exists but is not a LAWYER: " + req.getLawyerId());
            }
            lawyerName = lawyer.name();
        }

        // 3. Save the Case (If we reached here, validations passed)
        Case saved = caseRepo.save(Case.builder()
                .citizenId(req.getCitizenId())
                .lawyerId(req.getLawyerId())
                .title(req.getTitle())
                .description(req.getDescription())
                .build());

        log.info("Case #{} filed successfully", saved.getCaseId());

        // 4. Audit and Notifications
        performPostFilingActions(saved, citizenUserId, lawyerName, req.getLawyerId());

        CaseResponse resp = CaseResponse.from(saved);
        resp.setCitizenName(citizenName);
        resp.setLawyerName(lawyerName);
        return resp;
    }

    private void performPostFilingActions(Case saved, Long citizenUserId, String lawyerName, Long lawyerId) {
        if (citizenUserId != null) {
            audit(citizenUserId, "CASE_FILED", "Case:" + saved.getCaseId());
        }

        if (lawyerId != null && lawyerName != null) {
            if (citizenUserId != null) {
                notify(citizenUserId, saved.getCaseId(), "CASE",
                        "Your case \"" + saved.getTitle() + "\" filed. Lawyer " + lawyerName + " linked.");
            }
            notify(lawyerId, saved.getCaseId(), "CASE",
                    "You are linked as lawyer for Case #" + saved.getCaseId());
        } else if (citizenUserId != null) {
            notify(citizenUserId, saved.getCaseId(), "CASE",
                    "Case \"" + saved.getTitle() + "\" filed successfully.");
        }
    }

    @Override
    public CaseResponse getCaseById(Long id) {
        return enrich(caseRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found: " + id)));
    }

    @Override
    public List<CaseResponse> getAllCases() {
        return caseRepo.findAll().stream().map(this::enrich).toList();
    }

    @Override
    public List<CaseResponse> getCasesByCitizen(Long citizenId) {
        return caseRepo.findByCitizenId(citizenId).stream().map(this::enrich).toList();
    }

    @Override
    public List<CaseResponse> getCasesByLawyer(Long lawyerId) {
        return caseRepo.findByLawyerId(lawyerId).stream().map(this::enrich).toList();
    }

    @Override
    public List<CaseResponse> getCasesByStatus(Case.CaseStatus status) {
        return caseRepo.findByStatus(status).stream().map(CaseResponse::from).toList();
    }

    @Override
    @Transactional
    public CaseResponse updateCaseStatus(Long id, Case.CaseStatus status) {
        Case c = caseRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Case not found: " + id));
        Case.CaseStatus old = c.getStatus();
        c.setStatus(status);
        Case updated = caseRepo.save(c);
        // Notify asynchronously — don't block on Feign failures
        try {
            var citizen = citizenClient.getCitizenById(updated.getCitizenId());
            if (citizen.userId() != null) {
                audit(citizen.userId(), "CASE_STATUS_UPDATED", "Case:" + id + " [" + old + " -> " + status + "]");
                notify(citizen.userId(), id, "CASE", "Your case \"" + updated.getTitle() + "\" status: " + status);
            }
        } catch (Exception e) {
            log.warn("Notify/audit failed on status update: {}", e.getMessage());
        }
        return CaseResponse.from(updated);
    }

    //    @Override
//    @Transactional
//    public CaseResponse assignLawyer(Long caseId, Long lawyerId) {
//        Case c = caseRepo.findById(caseId).orElseThrow(() -> new ResourceNotFoundException("Case not found: " + caseId));
//        IdentityFeignClient.UserDto lawyer = null;
//        CitizenFeignClient.CitizenDto citizen = null;
//        try {
//             lawyer = identityClient.getUserById(lawyerId);
//             citizen = citizenClient.getCitizenById(c.getCitizenId());
//            if (!"LAWYER".equals(lawyer.role()))
//                throw new BadRequestException("User is not a LAWYER: " + lawyerId);
//            notify(lawyerId, caseId, "CASE", "You are assigned as lawyer for Case #" + caseId + ": \"" + c.getTitle() + "\"");
//        } catch (BadRequestException e) { throw e; }
//        catch (Exception e) { log.warn("Could not verify/notify lawyer: {}", e.getMessage()); }
//        c.setLawyerId(lawyerId);
//        CaseResponse caseResponse = CaseResponse.from(caseRepo.save(c));
//        caseResponse.setCitizenName(citizen.name());
//        caseResponse.setLawyerName(lawyer.name());
//
//        return caseResponse;
//    }
    @Override
    @Transactional
    public CaseResponse assignLawyer(Long caseId, Long lawyerId) {
        Case c = caseRepo.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found: " + caseId));

        try {
            var lawyerDto = identityClient.getUserById(lawyerId);
            if (lawyerDto == null || !"LAWYER".equals(lawyerDto.role())) {
                throw new BadRequestException("User is not a LAWYER: " + lawyerId);
            }

            c.setLawyerId(lawyerId);
            Case saved = caseRepo.save(c);

            notify(lawyerId, caseId, "CASE", "You are assigned as lawyer for Case #" + caseId);

            return enrich(saved); // Safely fetches names

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Lawyer assignment failed: {}", e.getMessage());
            return enrich(c);
        }
    }

    @Override
    @Transactional
    public CaseResponse removeLawyer(Long caseId) {
        Case c = caseRepo.findById(caseId).orElseThrow(() -> new ResourceNotFoundException("Case not found: " + caseId));
        if (c.getLawyerId() != null) {
            try {
                notify(c.getLawyerId(), caseId, "CASE", "You have been removed as lawyer from Case #" + caseId);
            } catch (Exception e) {
                log.warn("Notify failed: {}", e.getMessage());
            }
        }
        c.setLawyerId(null);
        return CaseResponse.from(caseRepo.save(c));
    }

    //    @Override
//    @Transactional
//    public CaseResponse assignJudge(Long caseId, Long judgeId) {
//        Case c = caseRepo.findById(caseId).orElseThrow(() -> new ResourceNotFoundException("Case not found: " + caseId));
//        IdentityFeignClient.UserDto judge = null;
//        try {
//             judge = identityClient.getUserById(judgeId);
//            if (!"JUDGE".equals(judge.role()))
//                throw new BadRequestException("User is not a JUDGE: " + judgeId);
//            notify(judgeId, caseId, "CASE", "You are assigned as Judge for Case #" + caseId + ": \"" + c.getTitle() + "\"");
//            audit(judgeId, "JUDGE_ASSIGNED", "Case:" + caseId);
//        } catch (BadRequestException e) { throw e; }
//        catch (Exception e) { log.warn("Could not verify/notify judge: {}", e.getMessage()); }
//        c.setJudgeId(judgeId);
//        c.setStatus(Case.CaseStatus.ACTIVE);
//        CaseResponse caseResponse = CaseResponse.from(caseRepo.save(c));
//        caseResponse.setJudgeName(judge.name());
//        caseResponse.setLawyerName(identityClient.getUserById(c.getLawyerId()).name());
//        return caseResponse;
//    }
    @Override
    @Transactional
    public CaseResponse assignJudge(Long caseId, Long judgeId) {
        Case c = caseRepo.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found: " + caseId));

        try {
            var judgeDto = identityClient.getUserById(judgeId);
            if (judgeDto == null || !"JUDGE".equals(judgeDto.role())) {
                throw new BadRequestException("User is not a JUDGE: " + judgeId);
            }

            // Update Entity
            c.setJudgeId(judgeId);
            c.setStatus(Case.CaseStatus.ACTIVE);
            Case saved = caseRepo.save(c);

            // Async actions
            notify(judgeId, caseId, "CASE", "You are assigned as Judge for Case #" + caseId);
            audit(judgeId, "JUDGE_ASSIGNED", "Case:" + caseId);

            // Use enrich() to safely get Judge, Lawyer, and Citizen names
            return enrich(saved);

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Judge assignment failed: {}", e.getMessage());
            // If Feign fails, we still return the saved case via enrich (which handles nulls)
            return enrich(c);
        }
    }

    @Override
    public DocumentResponse addDocument(Long caseId, DocumentRequest req) {
        Case c = caseRepo.findById(caseId).orElseThrow(() -> new ResourceNotFoundException("Case not found: " + caseId));
        CaseDocument doc = CaseDocument.builder().caseEntity(c).docType(req.getDocType()).fileUri(req.getFileUri()).build();
        return DocumentResponse.from(docRepo.save(doc));
    }

    @Override
    public List<DocumentResponse> getDocuments(Long caseId) {
        return docRepo.findByCaseEntityCaseId(caseId).stream().map(DocumentResponse::from).toList();
    }

    @Override
    public DocumentResponse verifyDocument(Long docId, CaseDocument.VerificationStatus status) {
        CaseDocument doc = docRepo.findById(docId).orElseThrow(() -> new ResourceNotFoundException("Document not found: " + docId));
        doc.setVerificationStatus(status);
        return DocumentResponse.from(docRepo.save(doc));
    }

    /**
     * Enrich a Case with names fetched via Feign (best-effort, never fails the request)
     */
    private CaseResponse enrich(Case c) {
        CaseResponse r = CaseResponse.from(c);
        try {
            r.setCitizenName(citizenClient.getCitizenById(c.getCitizenId()).name());
        } catch (Exception e) {
            log.debug("Cannot enrich citizenName: {}", e.getMessage());
        }
        if (c.getLawyerId() != null) {
            try {
                r.setLawyerName(identityClient.getUserById(c.getLawyerId()).name());
            } catch (Exception e) {
                log.debug("Cannot enrich lawyerName");
            }
        }
        if (c.getJudgeId() != null) {
            try {
                r.setJudgeName(identityClient.getUserById(c.getJudgeId()).name());
            } catch (Exception e) {
                log.debug("Cannot enrich judgeName");
            }
        }
        return r;
    }

    private void notify(Long userId, Long entityId, String category, String message) {
        try {
            notifClient.send(Map.of("userId", userId, "entityId", entityId, "category", category, "message", message));
        } catch (Exception e) {
            log.warn("Notify failed userId={}: {}", userId, e.getMessage());
        }
    }

    private void audit(Long userId, String action, String resource) {
        try {
            auditClient.log(userId, action, resource);
        } catch (Exception e) {
            log.warn("Audit failed: {}", e.getMessage());
        }
    }
}

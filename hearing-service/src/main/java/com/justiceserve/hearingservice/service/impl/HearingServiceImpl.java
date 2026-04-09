package com.justiceserve.hearingservice.service.impl;

import com.justiceserve.hearingservice.dto.*;
import com.justiceserve.hearingservice.entity.*;
import com.justiceserve.hearingservice.exception.BadRequestException;
import com.justiceserve.hearingservice.exception.ResourceNotFoundException;
import com.justiceserve.hearingservice.feign.*;
import com.justiceserve.hearingservice.repository.*;
import com.justiceserve.hearingservice.service.HearingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class HearingServiceImpl implements HearingService {

    private final HearingRepository hearingRepo;
    private final ProceedingRepository proceedingRepo;
    private final IdentityFeignClient identityClient;   // fetches judge from identity-service
    private final NotificationFeignClient notifClient;
    private final AuditLogFeignClient auditClient;
    private final CaseFeignClient caseClient;

    @Override
    @Transactional
    public HearingResponse scheduleHearing(HearingRequest req) {
        log.info("Scheduling hearing caseId={}, judgeId={}", req.getCaseId(), req.getJudgeId());

        // Verify judge exists and has JUDGE role in identity-service — no local users table
        String judgeName = null;
        try {
            var judge = identityClient.getUserById(req.getJudgeId());
            if (!"JUDGE".equals(judge.role()))
                throw new BadRequestException("User is not a JUDGE: " + req.getJudgeId());
            judgeName = judge.name();
        } catch (BadRequestException e) { throw e; }
        catch (Exception e) { log.warn("Could not verify judge: {}", e.getMessage()); }

        Hearing saved = hearingRepo.save(Hearing.builder()
                .caseId(req.getCaseId())
                .judgeId(req.getJudgeId())
                .judgeName(judgeName)              // cached here for display
                .citizenUserId(req.getCitizenUserId())
                .lawyerUserId(req.getLawyerUserId())
                .date(req.getDate())
                .time(req.getTime())
                .caseTitle(req.getCaseTitle())
                .build());

        // Update case status via Feign
        try { caseClient.updateStatus(req.getCaseId(), "HEARING_SCHEDULED"); }
        catch (Exception e) { log.warn("Case status update failed: {}", e.getMessage()); }

        if (req.getJudgeId() != null)
            audit(req.getJudgeId(), "HEARING_SCHEDULED", "Hearing:" + saved.getHearingId() + " Case:" + req.getCaseId());

        String msg = "Hearing for case \"" + req.getCaseTitle() + "\" on " + req.getDate() + " at " + req.getTime()
                + ". Judge: " + (judgeName != null ? judgeName : "TBD");
        notify(req.getCitizenUserId(), saved.getHearingId(), "HEARING", msg + " Please be present.");
        if (req.getLawyerUserId() != null)
            notify(req.getLawyerUserId(), saved.getHearingId(), "HEARING", msg + " Please prepare.");

        return HearingResponse.from(saved);
    }

    @Override
    public HearingResponse getHearingById(Long id) {
        return HearingResponse.from(hearingRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hearing not found: " + id)));
    }

    @Override public List<HearingResponse> getAllHearings() {
        return hearingRepo.findAll().stream().map(HearingResponse::from).toList();
    }

    @Override public List<HearingResponse> getHearingsByCase(Long caseId) {
        return hearingRepo.findByCaseId(caseId).stream().map(HearingResponse::from).toList();
    }

    @Override public List<HearingResponse> getHearingsByJudge(Long judgeId) {
        return hearingRepo.findByJudgeId(judgeId).stream().map(HearingResponse::from).toList();
    }

    @Override
    @Transactional
    public HearingResponse updateStatus(Long id, Hearing.HearingStatus status) {
        Hearing h = hearingRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hearing not found: " + id));
        Hearing.HearingStatus old = h.getStatus();
        h.setStatus(status);
        Hearing saved = hearingRepo.save(h);
        audit(saved.getJudgeId(), "HEARING_STATUS_UPDATED", "Hearing:" + id + " [" + old + " -> " + status + "]");
        notify(saved.getCitizenUserId(), id, "HEARING", "Hearing #" + id + " status: " + status);
        return HearingResponse.from(saved);
    }

    @Override
    @Transactional
    public ProceedingResponse addProceeding(ProceedingRequest req) {
        Hearing h = hearingRepo.findById(req.getHearingId())
                .orElseThrow(() -> new ResourceNotFoundException("Hearing not found: " + req.getHearingId()));
        Proceeding p = Proceeding.builder().hearing(h).notes(req.getNotes()).status(req.getStatus()).build();
        Proceeding saved = proceedingRepo.save(p);
        audit(h.getJudgeId(), "PROCEEDING_ADDED", "Proceeding:" + saved.getProceedingId() + " Hearing:" + req.getHearingId());
        return ProceedingResponse.from(saved);
    }

    @Override
    public List<ProceedingResponse> getProceedingsByHearing(Long hearingId) {
        return proceedingRepo.findByHearingHearingId(hearingId).stream().map(ProceedingResponse::from).toList();
    }

    private void notify(Long userId, Long entityId, String category, String message) {
        try { notifClient.send(Map.of("userId", userId, "entityId", entityId, "category", category, "message", message)); }
        catch (Exception e) { log.warn("Notify failed: {}", e.getMessage()); }
    }

    private void audit(Long userId, String action, String resource) {
        try { auditClient.log(userId, action, resource); }
        catch (Exception e) { log.warn("Audit failed: {}", e.getMessage()); }
    }
}

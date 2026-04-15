package com.justiceserve.judgmentservice.service.impl;

import com.justiceserve.judgmentservice.dto.*;
import com.justiceserve.judgmentservice.entity.*;
import com.justiceserve.judgmentservice.exception.ResourceNotFoundException;
import com.justiceserve.judgmentservice.feign.*;
import com.justiceserve.judgmentservice.repository.*;
import com.justiceserve.judgmentservice.service.JudgmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class JudgmentServiceImpl implements JudgmentService {
    private final JudgmentRepository judgmentRepo;
    private final CourtOrderRepository orderRepo;
    private final NotificationFeignClient notifClient;
    private final AuditFeignClient auditClient;
    private final CaseFeignClient caseClient;

    @Transactional
    public JudgmentResponse recordJudgment(JudgmentRequest req) {
        log.info("Recording judgment caseId={}", req.getCaseId());
        Judgment j = Judgment.builder().caseId(req.getCaseId()).judgeId(req.getJudgeId())
                .citizenUserId(req.getCitizenUserId()).lawyerUserId(req.getLawyerUserId())
                .caseTitle(req.getCaseTitle()).summary(req.getSummary()).build();
        Judgment saved = judgmentRepo.save(j);
        try {
            caseClient.updateStatus(req.getCaseId(), "JUDGMENT_PENDING");
        } catch (Exception e) {
            log.warn("Case update failed: {}", e.getMessage());
        }
        audit(req.getJudgeId(), "JUDGMENT_RECORDED", "Judgment:" + saved.getJudgmentId() + " (DRAFT) Case:" + req.getCaseId());
        if (req.getCitizenUserId() != null)
            notify(req.getCitizenUserId(), saved.getJudgmentId(), "JUDGMENT", "A draft judgment was recorded for your case \"" + req.getCaseTitle() + "\"");
        return JudgmentResponse.from(saved);
    }

    public JudgmentResponse getById(Long id) {
        return JudgmentResponse.from(judgmentRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Judgment not found: " + id)));
    }

    public List<JudgmentResponse> getAll() {
        return judgmentRepo.findAll().stream().map(JudgmentResponse::from).toList();
    }

    public List<JudgmentResponse> getByCase(Long caseId) {
        return judgmentRepo.findByCaseId(caseId).stream().map(JudgmentResponse::from).toList();
    }

    @Transactional
    public JudgmentResponse finalizeJudgment(Long id) {
        log.info("Finalizing judgment #{}", id);
        Judgment j = judgmentRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Judgment not found: " + id));
        j.setStatus(Judgment.JudgmentStatus.FINAL);
        Judgment saved = judgmentRepo.save(j);
        try {
            caseClient.updateStatus(saved.getCaseId(), "CLOSED");
        } catch (Exception e) {
            log.warn("Case close failed: {}", e.getMessage());
        }
        audit(saved.getJudgeId(), "JUDGMENT_FINALIZED", "Judgment:" + id + " FINAL - Case:" + saved.getCaseId() + " CLOSED");
        if (saved.getCitizenUserId() != null)
            notify(saved.getCitizenUserId(), id, "JUDGMENT", "Your case \"" + saved.getCaseTitle() + "\" judgment is FINAL. Case CLOSED.");
        if (saved.getLawyerUserId() != null)
            notify(saved.getLawyerUserId(), id, "JUDGMENT", "Judgment finalized for case \"" + saved.getCaseTitle() + "\"");
        return JudgmentResponse.from(saved);
    }

    @Transactional
    public CourtOrderResponse issueOrder(CourtOrderRequest req) {
        CourtOrder o = CourtOrder.builder().caseId(req.getCaseId()).judgeId(req.getJudgeId())
                .citizenUserId(req.getCitizenUserId()).lawyerUserId(req.getLawyerUserId())
                .description(req.getDescription()).build();
        CourtOrder saved = orderRepo.save(o);
        audit(req.getJudgeId(), "COURT_ORDER_ISSUED", "CourtOrder:" + saved.getOrderId() + " Case:" + req.getCaseId());
        if (req.getCitizenUserId() != null)
            notify(req.getCitizenUserId(), saved.getOrderId(), "JUDGMENT", "Court Order #" + saved.getOrderId() + " issued for your case.");
        if (req.getLawyerUserId() != null)
            notify(req.getLawyerUserId(), saved.getOrderId(), "JUDGMENT", "Court Order #" + saved.getOrderId() + " issued.");
        return CourtOrderResponse.from(saved);
    }

    public CourtOrderResponse getOrderById(Long id) {
        return CourtOrderResponse.from(orderRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id)));
    }

    public List<CourtOrderResponse> getAllOrders() {
        return orderRepo.findAll().stream().map(CourtOrderResponse::from).toList();
    }

    public List<CourtOrderResponse> getOrdersByCase(Long caseId) {
        return orderRepo.findByCaseId(caseId).stream().map(CourtOrderResponse::from).toList();
    }

    @Transactional
    public CourtOrderResponse updateOrderStatus(Long id, CourtOrder.OrderStatus status) {
        CourtOrder o = orderRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
        o.setStatus(status);
        CourtOrder saved = orderRepo.save(o);
        audit(saved.getJudgeId(), "COURT_ORDER_STATUS_UPDATED", "CourtOrder:" + id + " -> " + status);
        return CourtOrderResponse.from(saved);
    }

    private void notify(Long userId, Long entityId, String category, String message) {
        try {
            notifClient.send(Map.of("userId", userId, "entityId", entityId, "category", category, "message", message));
        } catch (Exception e) {
            log.warn("Notify failed: {}", e.getMessage());
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
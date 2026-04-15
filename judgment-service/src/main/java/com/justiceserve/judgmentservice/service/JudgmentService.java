package com.justiceserve.judgmentservice.service;

import com.justiceserve.judgmentservice.dto.*;
import com.justiceserve.judgmentservice.entity.CourtOrder;
import java.util.List;

public interface JudgmentService {

    // --- Judgment Operations ---

    /**
     * Records a new draft judgment for a case.
     */
    JudgmentResponse recordJudgment(JudgmentRequest req);

    /**
     * Retrieves a specific judgment by its ID.
     */
    JudgmentResponse getById(Long id);

    /**
     * Retrieves all judgments in the system.
     */
    List<JudgmentResponse> getAll();

    /**
     * Retrieves all judgments associated with a specific case.
     */
    List<JudgmentResponse> getByCase(Long caseId);

    /**
     * Finalizes a draft judgment and closes the associated case.
     */
    JudgmentResponse finalizeJudgment(Long id);


    // --- Court Order Operations ---

    /**
     * Issues a new court order for a case.
     */
    CourtOrderResponse issueOrder(CourtOrderRequest req);

    /**
     * Retrieves a specific court order by its ID.
     */
    CourtOrderResponse getOrderById(Long id);

    /**
     * Retrieves all court orders in the system.
     */
    List<CourtOrderResponse> getAllOrders();

    /**
     * Retrieves all court orders associated with a specific case.
     */
    List<CourtOrderResponse> getOrdersByCase(Long caseId);

    /**
     * Updates the status of an existing court order (e.g., PENDING to SERVED).
     */
    CourtOrderResponse updateOrderStatus(Long id, CourtOrder.OrderStatus status);
}
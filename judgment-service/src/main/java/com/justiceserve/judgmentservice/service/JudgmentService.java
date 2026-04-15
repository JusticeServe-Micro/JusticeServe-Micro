package com.justiceserve.judgmentservice.service;

import com.justiceserve.judgmentservice.dto.CourtOrderRequest;
import com.justiceserve.judgmentservice.dto.CourtOrderResponse;
import com.justiceserve.judgmentservice.dto.JudgmentRequest;
import com.justiceserve.judgmentservice.dto.JudgmentResponse;
import com.justiceserve.judgmentservice.entity.CourtOrder;

import java.util.List;

public interface JudgmentService {


    JudgmentResponse recordJudgment(JudgmentRequest req);


    JudgmentResponse getById(Long id);

    List<JudgmentResponse> getAll();

    List<JudgmentResponse> getByCase(Long caseId);


    JudgmentResponse finalizeJudgment(Long id);



    CourtOrderResponse issueOrder(CourtOrderRequest req);

    CourtOrderResponse getOrderById(Long id);


    List<CourtOrderResponse> getAllOrders();

    List<CourtOrderResponse> getOrdersByCase(Long caseId);

    CourtOrderResponse updateOrderStatus(Long id, CourtOrder.OrderStatus status);
}
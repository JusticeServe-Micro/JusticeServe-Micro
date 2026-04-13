package com.justiceserve.judgmentservice.dto;
import com.justiceserve.judgmentservice.entity.CourtOrder;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CourtOrderResponse {
    private Long orderId;
    private Long caseId;
    private Long judgeId;
    private String description;
    private LocalDate date;
    private CourtOrder.OrderStatus status;

    public static CourtOrderResponse from(CourtOrder o) {
        CourtOrderResponse r = new CourtOrderResponse();
        r.orderId=o.getOrderId();
        r.caseId=o.getCaseId();
        r.judgeId=o.getJudgeId();
        r.description=o.getDescription();
        r.date=o.getDate();
        r.status=o.getStatus();
        return r;
    }
}
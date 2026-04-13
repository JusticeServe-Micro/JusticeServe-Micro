package com.justiceserve.judgmentservice.dto;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CourtOrderRequest {
    @NotNull
    private Long caseId;

    @NotNull
    private Long judgeId;

    private Long citizenUserId;
    private Long lawyerUserId;
    private String description;
}
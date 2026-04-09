package com.justiceserve.hearingservice.dto;
import lombok.Data;
@Data
public class ProceedingRequest {
    private Long hearingId;
    private String notes;
    private String status;
}
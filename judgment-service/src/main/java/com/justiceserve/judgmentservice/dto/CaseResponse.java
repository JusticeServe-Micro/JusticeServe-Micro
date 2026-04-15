package com.justiceserve.judgmentservice.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CaseResponse {
    private Long caseId;
    private Long citizenId;
    private String citizenName;     // enriched via Feign, null if unavailable
    private Long lawyerId;
    private String lawyerName;      // enriched via Feign, null if unavailable
    private Long judgeId;
    private String judgeName;       // enriched via Feign, null if unavailable
    private String title;
    private String description;
    private LocalDate filedDate;
    private String status;
    private LocalDateTime createdAt;

}

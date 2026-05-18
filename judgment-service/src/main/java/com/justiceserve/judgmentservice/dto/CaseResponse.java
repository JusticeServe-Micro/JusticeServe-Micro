package com.justiceserve.judgmentservice.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CaseResponse {
    private Long caseId;
    private Long citizenId;
    private String citizenName;
    private Long lawyerId;
    private String lawyerName;
    private Long judgeId;
    private String judgeName;
    private String title;
    private String description;
    private LocalDate filedDate;
    private String status;
    private LocalDateTime createdAt;

}

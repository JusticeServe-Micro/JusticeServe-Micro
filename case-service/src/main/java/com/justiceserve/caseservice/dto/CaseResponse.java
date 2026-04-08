package com.justiceserve.caseservice.dto;

import com.justiceserve.caseservice.entity.Case;
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

    public static CaseResponse from(Case c) {
        CaseResponse r = new CaseResponse();
        r.caseId = c.getCaseId();
        r.citizenId = c.getCitizenId();
        r.lawyerId = c.getLawyerId();
        r.judgeId = c.getJudgeId();
        r.title = c.getTitle();
        r.description = c.getDescription();
        r.filedDate = c.getFiledDate();
        r.status = c.getStatus().name();
        r.createdAt = c.getCreatedAt();
        return r;
    }
}

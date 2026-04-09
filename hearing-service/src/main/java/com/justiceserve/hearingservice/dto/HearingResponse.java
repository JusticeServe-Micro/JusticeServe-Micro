package com.justiceserve.hearingservice.dto;

import com.justiceserve.hearingservice.entity.Hearing;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class HearingResponse {
    private Long hearingId;
    private Long caseId;
    private Long judgeId;
    private String judgeName;
    private Long citizenUserId;
    private Long lawyerUserId;
    private LocalDate date;
    private LocalTime time;
    private Hearing.HearingStatus status;
    private String caseTitle;

    public static HearingResponse from(Hearing h) {
        HearingResponse r = new HearingResponse();
        r.hearingId = h.getHearingId();
        r.caseId = h.getCaseId();
        r.judgeId = h.getJudgeId();
        r.judgeName = h.getJudgeName();    // cached at schedule time
        r.citizenUserId = h.getCitizenUserId();
        r.lawyerUserId = h.getLawyerUserId();
        r.date = h.getDate();
        r.time = h.getTime();
        r.status = h.getStatus();
        r.caseTitle = h.getCaseTitle();
        return r;
    }
}

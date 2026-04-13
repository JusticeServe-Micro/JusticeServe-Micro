package com.justiceserve.judgmentservice.dto;
import com.justiceserve.judgmentservice.entity.Judgment;
import lombok.Data;
import java.time.LocalDate;

@Data
public class JudgmentResponse {
    private Long judgmentId;
    private Long caseId;
    private Long judgeId;
    private String summary;
    private LocalDate date;
    private Judgment.JudgmentStatus status;
    private String caseTitle;

    public static JudgmentResponse from(Judgment j) {
        JudgmentResponse r = new JudgmentResponse();
        r.judgmentId=j.getJudgmentId();
        r.caseId=j.getCaseId();
        r.judgeId=j.getJudgeId();
        r.summary=j.getSummary();
        r.date=j.getDate();
        r.status=j.getStatus();
        r.caseTitle=j.getCaseTitle();
        return r;
    }
}
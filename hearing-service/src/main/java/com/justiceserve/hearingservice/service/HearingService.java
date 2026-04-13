package com.justiceserve.hearingservice.service;
import com.justiceserve.hearingservice.dto.*;
import com.justiceserve.hearingservice.entity.Hearing;
import java.util.List;
public interface HearingService {
    HearingResponse scheduleHearing(HearingRequest req);
    HearingResponse getHearingById(Long id);
    List<HearingResponse> getAllHearings();
    List<HearingResponse> getHearingsByCase(Long caseId);
    List<HearingResponse> getHearingsByJudge(Long judgeId);
    HearingResponse updateStatus(Long id, Hearing.HearingStatus status);
    ProceedingResponse addProceeding(ProceedingRequest req);
    List<ProceedingResponse> getProceedingsByHearing(Long hearingId);
}
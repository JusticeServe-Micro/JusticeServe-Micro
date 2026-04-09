package com.justiceserve.hearingservice.dto;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
@Data
public class HearingRequest {
    @NotNull private Long caseId;
    @NotNull private Long judgeId;
    @NotNull private Long citizenUserId;
    private Long lawyerUserId;
    private String caseTitle;
    @NotNull private LocalDate date;
    @NotNull private LocalTime time;
}
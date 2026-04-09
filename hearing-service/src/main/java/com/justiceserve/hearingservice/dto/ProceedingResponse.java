package com.justiceserve.hearingservice.dto;
import com.justiceserve.hearingservice.entity.Proceeding;
import lombok.Data;
import java.time.LocalDate;
@Data
public class ProceedingResponse {
    private Long proceedingId; private Long hearingId; private String notes; private LocalDate date; private String status;
    public static ProceedingResponse from(Proceeding p) {
        ProceedingResponse r = new ProceedingResponse();
        r.proceedingId=p.getProceedingId(); r.hearingId=p.getHearing().getHearingId();
        r.notes=p.getNotes(); r.date=p.getDate(); r.status=p.getStatus();
        return r;
    }
}
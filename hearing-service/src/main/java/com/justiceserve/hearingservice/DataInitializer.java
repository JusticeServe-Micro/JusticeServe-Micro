package com.justiceserve.hearingservice;

import com.justiceserve.hearingservice.entity.Hearing;
import com.justiceserve.hearingservice.entity.Proceeding;
import com.justiceserve.hearingservice.repository.HearingRepository;
import com.justiceserve.hearingservice.repository.ProceedingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final HearingRepository hearingRepo;
    private final ProceedingRepository proceedingRepo;

    @Override
    public void run(String... args) {
        // Seed hearings
        Hearing hearing1 = seedHearing(1L, 2L, "Justice Sharma", 5L, 4L, LocalDate.now().plusDays(7), LocalTime.of(10, 0), "Property Dispute Case", Hearing.HearingStatus.SCHEDULED);
        Hearing hearing2 = seedHearing(2L, 9L, "Justice Kumar", 12L, 8L, LocalDate.now().plusDays(14), LocalTime.of(14, 0), "Criminal Assault Case", Hearing.HearingStatus.IN_PROGRESS);

        // Seed proceedings
        seedProceeding(hearing1, "Court opened, parties present", "IN_PROGRESS");
        seedProceeding(hearing2, "Witness testimony recorded", "COMPLETED");
    }

    private Hearing seedHearing(Long caseId, Long judgeId, String judgeName, Long citizenUserId, Long lawyerUserId, LocalDate date, LocalTime time, String caseTitle, Hearing.HearingStatus status) {
        Hearing h = Hearing.builder()
                .caseId(caseId)
                .judgeId(judgeId)
                .judgeName(judgeName)
                .citizenUserId(citizenUserId)
                .lawyerUserId(lawyerUserId)
                .date(date)
                .time(time)
                .caseTitle(caseTitle)
                .status(status)
                .build();
        hearingRepo.save(h);
        log.info("Seeded hearing: {}", caseTitle);
        return h;
    }

    private void seedProceeding(Hearing hearing, String notes, String status) {
        Proceeding p = Proceeding.builder()
                .hearing(hearing)
                .notes(notes)
                .status(status)
                .build();
        proceedingRepo.save(p);
        log.info("Seeded proceeding: {} for hearing {}", notes, hearing.getCaseTitle());
    }
}

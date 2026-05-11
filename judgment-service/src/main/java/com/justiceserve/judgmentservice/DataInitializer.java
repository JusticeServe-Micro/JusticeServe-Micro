package com.justiceserve.judgmentservice;

import com.justiceserve.judgmentservice.entity.CourtOrder;
import com.justiceserve.judgmentservice.entity.Judgment;
import com.justiceserve.judgmentservice.repository.CourtOrderRepository;
import com.justiceserve.judgmentservice.repository.JudgmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final JudgmentRepository judgmentRepo;
    private final CourtOrderRepository courtOrderRepo;

    @Override
    public void run(String... args) {
        // Seed judgments
        Judgment judgment1 = seedJudgment(1L, 2L, 5L, 4L, "Property Dispute Case", "Judgment in favor of plaintiff. Property awarded to citizen.");
        Judgment judgment2 = seedJudgment(2L, 9L, 12L, 8L, "Criminal Assault Case", "Guilty verdict. Defendant sentenced to 2 years.");

        // Seed court orders
        seedCourtOrder(1L, 2L, 5L, 4L, "Order to transfer property", CourtOrder.OrderStatus.ACTIVE);
        seedCourtOrder(2L, 9L, 12L, 8L, "Order for imprisonment", CourtOrder.OrderStatus.SERVED);
    }

    private Judgment seedJudgment(Long caseId, Long judgeId, Long citizenUserId, Long lawyerUserId, String caseTitle, String summary) {
        Judgment j = Judgment.builder()
                .caseId(caseId)
                .judgeId(judgeId)
                .citizenUserId(citizenUserId)
                .lawyerUserId(lawyerUserId)
                .caseTitle(caseTitle)
                .summary(summary)
                .status(Judgment.JudgmentStatus.FINAL)
                .build();
        judgmentRepo.save(j);
        log.info("Seeded judgment: {}", summary);
        return j;
    }

    private void seedCourtOrder(Long caseId, Long judgeId, Long citizenUserId, Long lawyerUserId, String description, CourtOrder.OrderStatus status) {
        CourtOrder o = CourtOrder.builder()
                .caseId(caseId)
                .judgeId(judgeId)
                .citizenUserId(citizenUserId)
                .lawyerUserId(lawyerUserId)
                .description(description)
                .status(status)
                .build();
        courtOrderRepo.save(o);
        log.info("Seeded court order: {} for case {}", description, caseId);
    }
}

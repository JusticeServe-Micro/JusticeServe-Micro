package com.justiceserve.reportservice;

import com.justiceserve.reportservice.entity.Report;
import com.justiceserve.reportservice.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ReportRepository reportRepo;

    @Override
    public void run(String... args) {
        seedReport(Report.ReportScope.CASE, "Summary of cases filed in January 2024", 1L);
        seedReport(Report.ReportScope.COMPLIANCE, "Compliance check results for Q1", 6L);
        seedReport(Report.ReportScope.HEARING, "Upcoming hearings for the week", 2L);
    }

    private void seedReport(Report.ReportScope scope, String metrics, Long generatedBy) {
        Report r = Report.builder()
                .scope(scope)
                .metrics(metrics)
                .generatedBy(generatedBy)
                .build();
        reportRepo.save(r);
        log.info("Seeded report: {}", metrics);
    }
}

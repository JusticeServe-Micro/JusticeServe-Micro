package com.justiceserve.citizenservice;

import com.justiceserve.citizenservice.entity.Citizen;
import com.justiceserve.citizenservice.entity.CitizenDocument;
import com.justiceserve.citizenservice.repository.CitizenDocumentRepository;
import com.justiceserve.citizenservice.repository.CitizenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CitizenRepository citizenRepo;
    private final CitizenDocumentRepository citizenDocRepo;

    @Override
    public void run(String... args) {
        // Seed citizens
        Citizen citizen1 = seedCitizen(5L, "Rahul Citizen", LocalDate.of(1990, 5, 15), "Male", "Mumbai, India", "rahul@example.com");
        Citizen citizen2 = seedCitizen(12L, "Priya Sharma", LocalDate.of(1985, 3, 20), "Female", "Delhi, India", "priya@example.com");
        seedCitizen(13L, "Arjun Kumar", LocalDate.of(1992, 7, 10), "Male", "Bangalore, India", "arjun@example.com");

        // Seed documents for citizens
        seedCitizenDocument(citizen1, CitizenDocument.DocType.ID_PROOF, "aadhar_rahul.pdf", CitizenDocument.VerificationStatus.VERIFIED);
        seedCitizenDocument(citizen2, CitizenDocument.DocType.LEGAL_DOC, "pan_priya.pdf", CitizenDocument.VerificationStatus.PENDING);
    }

    private Citizen seedCitizen(Long userId, String name, LocalDate dob, String gender, String address, String contactInfo) {
        Citizen c = Citizen.builder()
                .userId(userId)
                .name(name)
                .dob(dob)
                .gender(gender)
                .address(address)
                .contactInfo(contactInfo)
                .status(Citizen.Status.ACTIVE)
                .build();
        citizenRepo.save(c);
        log.info("Seeded citizen: {}", name);
        return c;
    }

    private void seedCitizenDocument(Citizen citizen, CitizenDocument.DocType docType, String fileUri, CitizenDocument.VerificationStatus status) {
        CitizenDocument doc = CitizenDocument.builder()
                .citizen(citizen)
                .docType(docType)
                .fileUri(fileUri)
                .verificationStatus(status)
                .build();
        citizenDocRepo.save(doc);
        log.info("Seeded citizen document: {} for {}", fileUri, citizen.getName());
    }
}

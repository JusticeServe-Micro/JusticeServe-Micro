package com.justiceserve.citizenservice.service.impl;

import com.justiceserve.citizenservice.dto.*;
import com.justiceserve.citizenservice.entity.*;
import com.justiceserve.citizenservice.exception.*;
import com.justiceserve.citizenservice.feign.AuditFeignClient;
import com.justiceserve.citizenservice.feign.IdentityFeignClient;
import com.justiceserve.citizenservice.repository.*;
import com.justiceserve.citizenservice.service.CitizenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CitizenServiceImpl implements CitizenService {

    private final CitizenRepository citizenRepo;
    private final CitizenDocumentRepository docRepo;
    private final IdentityFeignClient identityClient;  // fetches user from identity-service
    private final AuditFeignClient auditClient;

    @Override
    public CitizenResponse createCitizen(CitizenRequest req) {
        // Verify the user exists in identity-service via Feign — no local users table
        try {
            var user = identityClient.getUserById(req.getUserId());
            if (user == null || user.userId() == null)
                throw new BadRequestException("User not found in identity-service: " + req.getUserId());
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Could not verify user in identity-service (continuing): {}", e.getMessage());
        }

        if (citizenRepo.existsByUserId(req.getUserId()))
            throw new BadRequestException("Citizen profile already exists for userId: " + req.getUserId());

        Citizen c = Citizen.builder()
                .userId(req.getUserId())       // stored as plain Long — no local User row
                .name(req.getName())
                .dob(req.getDob())
                .gender(req.getGender())
                .address(req.getAddress())
                .contactInfo(req.getContactInfo())
                .build();

        CitizenResponse saved = CitizenResponse.from(citizenRepo.save(c));
        log.info("Citizen created: citizenId={}, userId={}", saved.getCitizenId(), saved.getUserId());
        try { auditClient.log(req.getUserId(), "CITIZEN_PROFILE_CREATED", "Citizen:" + saved.getCitizenId()); }
        catch (Exception e) { log.warn("Audit failed: {}", e.getMessage()); }
        return saved;
    }

    @Override
    public CitizenResponse getCitizenById(Long id) {
        return CitizenResponse.from(citizenRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Citizen not found: " + id)));
    }

    @Override
    public CitizenResponse getCitizenByUserId(Long userId) {
        return CitizenResponse.from(citizenRepo.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Citizen not found for userId: " + userId)));
    }

    @Override
    public List<CitizenResponse> getAllCitizens() {
        return citizenRepo.findAll().stream().map(CitizenResponse::from).toList();
    }

    @Override
    public CitizenResponse updateCitizen(Long id, CitizenRequest req) {
        Citizen c = citizenRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Citizen not found: " + id));
        c.setName(req.getName());
        if (req.getDob() != null) c.setDob(req.getDob());
        if (req.getGender() != null) c.setGender(req.getGender());
        if (req.getAddress() != null) c.setAddress(req.getAddress());
        if (req.getContactInfo() != null) c.setContactInfo(req.getContactInfo());
        return CitizenResponse.from(citizenRepo.save(c));
    }

    @Override
    public DocumentResponse addDocument(Long citizenId, DocumentRequest req) {
        Citizen c = citizenRepo.findById(citizenId)
                .orElseThrow(() -> new ResourceNotFoundException("Citizen not found: " + citizenId));
        CitizenDocument doc = CitizenDocument.builder()
                .citizen(c).docType(req.getDocType()).fileUri(req.getFileUri()).build();
        return DocumentResponse.from(docRepo.save(doc));
    }

    @Override
    public List<DocumentResponse> getDocuments(Long citizenId) {
        return docRepo.findByCitizenCitizenId(citizenId).stream().map(DocumentResponse::from).toList();
    }

    @Override
    public DocumentResponse verifyDocument(Long docId, CitizenDocument.VerificationStatus status) {
        CitizenDocument doc = docRepo.findById(docId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found: " + docId));
        doc.setVerificationStatus(status);
        return DocumentResponse.from(docRepo.save(doc));
    }
}

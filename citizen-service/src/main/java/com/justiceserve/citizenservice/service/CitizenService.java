package com.justiceserve.citizenservice.service;

import com.justiceserve.citizenservice.dto.*;
import com.justiceserve.citizenservice.entity.CitizenDocument;
import java.util.List;

public interface CitizenService {
    CitizenResponse createCitizen(CitizenRequest req);
    CitizenResponse getCitizenById(Long id);
    CitizenResponse getCitizenByUserId(Long userId);
    List<CitizenResponse> getAllCitizens();
    CitizenResponse updateCitizen(Long id, CitizenRequest req);
    DocumentResponse addDocument(Long citizenId, DocumentRequest req);
    List<DocumentResponse> getDocuments(Long citizenId);
    DocumentResponse verifyDocument(Long docId, CitizenDocument.VerificationStatus status);
}

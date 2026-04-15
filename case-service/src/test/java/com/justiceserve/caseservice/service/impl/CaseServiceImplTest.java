package com.justiceserve.caseservice.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.justiceserve.caseservice.dto.*;
import com.justiceserve.caseservice.entity.*;
import com.justiceserve.caseservice.exception.*;
import com.justiceserve.caseservice.feign.*;
import com.justiceserve.caseservice.repository.*;

@ExtendWith(MockitoExtension.class)
class CaseServiceImplTest {

    @Mock
    private CaseRepository caseRepo;

    @Mock
    private CaseDocumentRepository docRepo;

    @Mock
    private CitizenFeignClient citizenClient;

    @Mock
    private IdentityFeignClient identityClient;

    @Mock
    private NotificationFeignClient notifClient;

    @Mock
    private AuditFeignClient auditClient;

    @InjectMocks
    private CaseServiceImpl caseService;

    @Test
    void testFileCase_Success() {
        // Arrange
        CaseRequest req = new CaseRequest();
        req.setCitizenId(1L);
        req.setLawyerId(2L);
        req.setTitle("Test Case");
        req.setDescription("Description");

        Case savedCase = Case.builder()
                .caseId(1L)
                .citizenId(1L)
                .lawyerId(2L)
                .title("Test Case")
                .description("Description")
                .status(Case.CaseStatus.FILED)
                .build();

        when(citizenClient.getCitizenById(1L)).thenReturn(new CitizenFeignClient.CitizenDto(1L, 10L, "Citizen Name", "email", "contact", "ACTIVE"));
        when(identityClient.getUserById(2L)).thenReturn(new IdentityFeignClient.UserDto(2L, "Lawyer Name", "lawyer@email", "phone", "LAWYER", "ACTIVE"));
        when(caseRepo.save(any(Case.class))).thenReturn(savedCase);

        // Act
        CaseResponse response = caseService.fileCase(req);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getCaseId());
        assertEquals("Citizen Name", response.getCitizenName());
        assertEquals("Lawyer Name", response.getLawyerName());
        verify(caseRepo).save(any(Case.class));
        verify(notifClient, times(2)).send(anyMap());
        verify(auditClient).log(eq(10L), anyString(), anyString());
    }

    @Test
    void testFileCase_CitizenNotFound() {
        // Arrange
        CaseRequest req = new CaseRequest();
        req.setCitizenId(1L);
        req.setTitle("Test Case");

        when(citizenClient.getCitizenById(1L)).thenReturn(null);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> caseService.fileCase(req));
    }

    @Test
    void testGetCaseById_Success() {
        // Arrange
        Case c = Case.builder().caseId(1L).citizenId(1L).title("Test").status(Case.CaseStatus.FILED).build();
        when(caseRepo.findById(1L)).thenReturn(Optional.of(c));
        when(citizenClient.getCitizenById(1L)).thenReturn(new CitizenFeignClient.CitizenDto(1L, 10L, "Name", "email", "contact", "ACTIVE"));

        // Act
        CaseResponse response = caseService.getCaseById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getCaseId());
        assertEquals("Name", response.getCitizenName());
    }

    @Test
    void testGetCaseById_NotFound() {
        // Arrange
        when(caseRepo.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> caseService.getCaseById(1L));
    }

    @Test
    void testGetAllCases() {
        // Arrange
        List<Case> cases = List.of(
                Case.builder().caseId(1L).citizenId(1L).title("Case1").status(Case.CaseStatus.FILED).build(),
                Case.builder().caseId(2L).citizenId(2L).title("Case2").status(Case.CaseStatus.ACTIVE).build()
        );
        when(caseRepo.findAll()).thenReturn(cases);
        when(citizenClient.getCitizenById(anyLong())).thenReturn(new CitizenFeignClient.CitizenDto(1L, 10L, "Name", "email", "contact", "ACTIVE"));

        // Act
        List<CaseResponse> responses = caseService.getAllCases();

        // Assert
        assertEquals(2, responses.size());
    }

    @Test
    void testGetCasesByCitizen() {
        // Arrange
        List<Case> cases = List.of(Case.builder().caseId(1L).citizenId(1L).title("Case").status(Case.CaseStatus.FILED).build());
        when(caseRepo.findByCitizenId(1L)).thenReturn(cases);
        when(citizenClient.getCitizenById(1L)).thenReturn(new CitizenFeignClient.CitizenDto(1L, 10L, "Name", "email", "contact", "ACTIVE"));

        // Act
        List<CaseResponse> responses = caseService.getCasesByCitizen(1L);

        // Assert
        assertEquals(1, responses.size());
    }

    @Test
    void testGetCasesByLawyer() {
        // Arrange
        List<Case> cases = List.of(Case.builder().caseId(1L).citizenId(1L).lawyerId(2L).title("Case").status(Case.CaseStatus.FILED).build());
        when(caseRepo.findByLawyerId(2L)).thenReturn(cases);
        when(citizenClient.getCitizenById(1L)).thenReturn(new CitizenFeignClient.CitizenDto(1L, 10L, "Name", "email", "contact", "ACTIVE"));
        when(identityClient.getUserById(2L)).thenReturn(new IdentityFeignClient.UserDto(2L, "Lawyer", "email", "phone", "LAWYER", "ACTIVE"));

        // Act
        List<CaseResponse> responses = caseService.getCasesByLawyer(2L);

        // Assert
        assertEquals(1, responses.size());
        assertEquals("Lawyer", responses.get(0).getLawyerName());
    }

    @Test
    void testGetCasesByStatus() {
        // Arrange
        List<Case> cases = List.of(Case.builder().caseId(1L).citizenId(1L).title("Case").status(Case.CaseStatus.FILED).build());
        when(caseRepo.findByStatus(Case.CaseStatus.FILED)).thenReturn(cases);

        // Act
        List<CaseResponse> responses = caseService.getCasesByStatus(Case.CaseStatus.FILED);

        // Assert
        assertEquals(1, responses.size());
    }

    @Test
    void testUpdateCaseStatus() {
        // Arrange
        Case c = Case.builder().caseId(1L).citizenId(1L).status(Case.CaseStatus.FILED).build();
        when(caseRepo.findById(1L)).thenReturn(Optional.of(c));
        when(caseRepo.save(any(Case.class))).thenReturn(c);
        when(citizenClient.getCitizenById(1L)).thenReturn(new CitizenFeignClient.CitizenDto(1L, 10L, "Name", "email", "contact", "ACTIVE"));

        // Act
        CaseResponse response = caseService.updateCaseStatus(1L, Case.CaseStatus.ACTIVE);

        // Assert
        assertEquals("ACTIVE", response.getStatus());
        verify(caseRepo).save(c);
    }

    @Test
    void testAssignLawyer_Success() {
        // Arrange
        Case c = Case.builder().caseId(1L).citizenId(1L).build();
        when(caseRepo.findById(1L)).thenReturn(Optional.of(c));
        when(identityClient.getUserById(2L)).thenReturn(new IdentityFeignClient.UserDto(2L, "Lawyer", "email", "phone", "LAWYER", "ACTIVE"));
        when(caseRepo.save(any(Case.class))).thenReturn(c);
        when(citizenClient.getCitizenById(1L)).thenReturn(new CitizenFeignClient.CitizenDto(1L, 10L, "Citizen", "email", "contact", "ACTIVE"));
        when(identityClient.getUserById(2L)).thenReturn(new IdentityFeignClient.UserDto(2L, "Lawyer", "email", "phone", "LAWYER", "ACTIVE"));

        // Act
        CaseResponse response = caseService.assignLawyer(1L, 2L);

        // Assert
        assertEquals(2L, response.getLawyerId());
        verify(caseRepo).save(c);
    }

    @Test
    void testAssignLawyer_InvalidRole() {
        // Arrange
        Case c = Case.builder().caseId(1L).citizenId(1L).build();
        when(caseRepo.findById(1L)).thenReturn(Optional.of(c));
        when(identityClient.getUserById(2L)).thenReturn(new IdentityFeignClient.UserDto(2L, "Lawyer", "email", "phone", "CITIZEN", "ACTIVE"));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> caseService.assignLawyer(1L, 2L));
    }

    @Test
    void testRemoveLawyer() {
        // Arrange
        Case c = Case.builder().caseId(1L).citizenId(1L).lawyerId(2L).build();
        when(caseRepo.findById(1L)).thenReturn(Optional.of(c));
        when(caseRepo.save(any(Case.class))).thenReturn(c);

        // Act
        CaseResponse response = caseService.removeLawyer(1L);

        // Assert
        assertNull(response.getLawyerId());
    }

    @Test
    void testAssignJudge_Success() {
        // Arrange
        Case c = Case.builder().caseId(1L).citizenId(1L).build();
        when(caseRepo.findById(1L)).thenReturn(Optional.of(c));
        when(identityClient.getUserById(3L)).thenReturn(new IdentityFeignClient.UserDto(3L, "Judge", "email", "phone", "JUDGE", "ACTIVE"));
        when(caseRepo.save(any(Case.class))).thenReturn(c);
        when(citizenClient.getCitizenById(1L)).thenReturn(new CitizenFeignClient.CitizenDto(1L, 10L, "Citizen", "email", "contact", "ACTIVE"));
        when(identityClient.getUserById(3L)).thenReturn(new IdentityFeignClient.UserDto(3L, "Judge", "email", "phone", "JUDGE", "ACTIVE"));

        // Act
        CaseResponse response = caseService.assignJudge(1L, 3L);

        // Assert
        assertEquals(3L, response.getJudgeId());
        assertEquals("ACTIVE", response.getStatus());
    }

    @Test
    void testAddDocument() {
        // Arrange
        Case c = Case.builder().caseId(1L).build();
        DocumentRequest req = new DocumentRequest();
        req.setDocType(CaseDocument.DocType.PETITION);
        req.setFileUri("uri");
        CaseDocument doc = CaseDocument.builder().documentId(1L).docType(CaseDocument.DocType.PETITION).fileUri("uri").verificationStatus(CaseDocument.VerificationStatus.PENDING).build();
        when(caseRepo.findById(1L)).thenReturn(Optional.of(c));
        when(docRepo.save(any(CaseDocument.class))).thenReturn(doc);

        // Act
        DocumentResponse response = caseService.addDocument(1L, req);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getDocumentId());
    }

    @Test
    void testGetDocuments() {
        // Arrange
        List<CaseDocument> docs = List.of(CaseDocument.builder().documentId(1L).docType(CaseDocument.DocType.PETITION).fileUri("uri").verificationStatus(CaseDocument.VerificationStatus.PENDING).build());
        when(docRepo.findByCaseEntityCaseId(1L)).thenReturn(docs);

        // Act
        List<DocumentResponse> responses = caseService.getDocuments(1L);

        // Assert
        assertEquals(1, responses.size());
    }

    @Test
    void testVerifyDocument() {
        // Arrange
        CaseDocument doc = CaseDocument.builder().documentId(1L).verificationStatus(CaseDocument.VerificationStatus.PENDING).build();
        when(docRepo.findById(1L)).thenReturn(Optional.of(doc));
        when(docRepo.save(any(CaseDocument.class))).thenReturn(doc);

        // Act
        DocumentResponse response = caseService.verifyDocument(1L, CaseDocument.VerificationStatus.VERIFIED);

        // Assert
        assertEquals("VERIFIED", response.getVerificationStatus());
    }
}

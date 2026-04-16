package com.justiceserve.citizenservice.service.impl;

import com.justiceserve.citizenservice.dto.*;
import com.justiceserve.citizenservice.entity.Citizen;
import com.justiceserve.citizenservice.entity.CitizenDocument;
import com.justiceserve.citizenservice.exception.BadRequestException;
import com.justiceserve.citizenservice.exception.ResourceNotFoundException;
import com.justiceserve.citizenservice.feign.AuditFeignClient;
import com.justiceserve.citizenservice.feign.IdentityFeignClient;
import com.justiceserve.citizenservice.repository.CitizenDocumentRepository;
import com.justiceserve.citizenservice.repository.CitizenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CitizenServiceImplTest {

    @Mock
    private CitizenRepository citizenRepo;

    @Mock
    private CitizenDocumentRepository docRepo;

    @Mock
    private IdentityFeignClient identityClient;

    @Mock
    private AuditFeignClient auditClient;

    @InjectMocks
    private CitizenServiceImpl citizenService;

    private CitizenRequest citizenRequest;
    private Citizen citizen;

    @BeforeEach
    void setUp() {
        citizenRequest = new CitizenRequest();
        citizenRequest.setUserId(1L);
        citizenRequest.setName("John Doe");
        citizenRequest.setDob(LocalDate.of(1990, 1, 1));

        citizen = Citizen.builder()
                .citizenId(101L)
                .userId(1L)
                .name("John Doe")
                .status(Citizen.Status.ACTIVE)
                .build();
    }

    @Test
    void createCitizen_Success() {
        // Arrange
        IdentityFeignClient.UserDto userDto = new IdentityFeignClient.UserDto(1L, "John", "john@test.com", "123", "CITIZEN", "ACTIVE");
        when(identityClient.getUserById(1L)).thenReturn(userDto);
        when(citizenRepo.existsByUserId(1L)).thenReturn(false);
        when(citizenRepo.save(any(Citizen.class))).thenReturn(citizen);

        // Act
        CitizenResponse response = citizenService.createCitizen(citizenRequest);

        // Assert
        assertNotNull(response);
        assertEquals("John Doe", response.getName());
        verify(auditClient, times(1)).log(eq(1L), eq("CITIZEN_PROFILE_CREATED"), anyString());
    }

    @Test
    void createCitizen_UserNotFoundInIdentityService_ThrowsException() {
        // Arrange
        when(identityClient.getUserById(1L)).thenReturn(null);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> citizenService.createCitizen(citizenRequest));
    }

    @Test
    void createCitizen_AlreadyExists_ThrowsException() {
        // Arrange
        IdentityFeignClient.UserDto userDto = new IdentityFeignClient.UserDto(1L, "John", "j@t.com", "123", "ROLE", "ACT");
        when(identityClient.getUserById(1L)).thenReturn(userDto);
        when(citizenRepo.existsByUserId(1L)).thenReturn(true);


        assertThrows(BadRequestException.class, () -> citizenService.createCitizen(citizenRequest));
    }

    @Test
    void getCitizenById_Success() {
        // Arrange
        when(citizenRepo.findById(101L)).thenReturn(Optional.of(citizen));

        // Act
        CitizenResponse response = citizenService.getCitizenById(101L);

        // Assert
        assertEquals(101L, response.getCitizenId());
    }

    @Test
    void getCitizenById_NotFound_ThrowsException() {
        // Arrange
        when(citizenRepo.findById(101L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> citizenService.getCitizenById(101L));
    }

    @Test
    void addDocument_Success() {
        // Arrange
        DocumentRequest docReq = new DocumentRequest();
        docReq.setDocType(CitizenDocument.DocType.ID_PROOF);
        docReq.setFileUri("http://storage/id.pdf");

        CitizenDocument savedDoc = CitizenDocument.builder()
                .documentId(500L)
                .docType(CitizenDocument.DocType.ID_PROOF)
                .verificationStatus(CitizenDocument.VerificationStatus.PENDING)
                .build();

        when(citizenRepo.findById(101L)).thenReturn(Optional.of(citizen));
        when(docRepo.save(any(CitizenDocument.class))).thenReturn(savedDoc);

        // Act
        DocumentResponse response = citizenService.addDocument(101L, docReq);

        // Assert
        assertNotNull(response);
        assertEquals("PENDING", response.getVerificationStatus());
    }

    @Test
    void verifyDocument_Success() {
        // Arrange
        CitizenDocument doc = CitizenDocument.builder()
                .documentId(500L)
                .verificationStatus(CitizenDocument.VerificationStatus.PENDING)
                .build();

        when(docRepo.findById(500L)).thenReturn(Optional.of(doc));
        when(docRepo.save(any(CitizenDocument.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        DocumentResponse response = citizenService.verifyDocument(500L, CitizenDocument.VerificationStatus.VERIFIED);

        // Assert
        assertEquals("VERIFIED", response.getVerificationStatus());
    }
}
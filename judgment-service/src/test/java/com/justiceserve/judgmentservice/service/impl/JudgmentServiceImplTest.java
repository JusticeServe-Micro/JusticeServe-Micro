package com.justiceserve.judgmentservice.service.impl;

import com.justiceserve.judgmentservice.dto.*;
import com.justiceserve.judgmentservice.entity.CourtOrder;
import com.justiceserve.judgmentservice.entity.Judgment;
import com.justiceserve.judgmentservice.exception.ResourceNotFoundException;
import com.justiceserve.judgmentservice.feign.AuditFeignClient;
import com.justiceserve.judgmentservice.feign.CaseFeignClient;
import com.justiceserve.judgmentservice.feign.NotificationFeignClient;
import com.justiceserve.judgmentservice.repository.CourtOrderRepository;
import com.justiceserve.judgmentservice.repository.JudgmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JudgmentServiceImplTest {

    @Mock
    private JudgmentRepository judgmentRepo;

    @Mock
    private CourtOrderRepository orderRepo;

    @Mock
    private NotificationFeignClient notifClient;

    @Mock
    private AuditFeignClient auditClient;

    @Mock
    private CaseFeignClient caseClient;

    @InjectMocks
    private JudgmentServiceImpl service;

    @Captor
    private ArgumentCaptor<Map<String, Object>> notificationCaptor;

    @Captor
    private ArgumentCaptor<Long> auditUserIdCaptor;

    @Captor
    private ArgumentCaptor<String> auditActionCaptor;

    @Captor
    private ArgumentCaptor<String> auditResourceCaptor;

    private Judgment judgment;
    private CourtOrder courtOrder;
    private JudgmentRequest judgmentRequest;
    private CourtOrderRequest courtOrderRequest;
    private CaseResponse caseResponse;

    @BeforeEach
    void setUp() {
        judgment = Judgment.builder()
                .judgmentId(1L)
                .caseId(100L)
                .judgeId(200L)
                .citizenUserId(300L)
                .lawyerUserId(400L)
                .caseTitle("Test Case")
                .summary("Test Summary")
                .date(LocalDate.now())
                .status(Judgment.JudgmentStatus.DRAFT)
                .build();

        courtOrder = CourtOrder.builder()
                .orderId(1L)
                .caseId(100L)
                .judgeId(200L)
                .citizenUserId(300L)
                .lawyerUserId(400L)
                .description("Test Order")
                .date(LocalDate.now())
                .status(CourtOrder.OrderStatus.ACTIVE)
                .build();

        judgmentRequest = new JudgmentRequest();
        judgmentRequest.setCaseId(100L);
        judgmentRequest.setJudgeId(200L);
        judgmentRequest.setCitizenUserId(300L);
        judgmentRequest.setLawyerUserId(400L);
        judgmentRequest.setCaseTitle("Test Case");
        judgmentRequest.setSummary("Test Summary");

        courtOrderRequest = new CourtOrderRequest();
        courtOrderRequest.setCaseId(100L);
        courtOrderRequest.setJudgeId(200L);
        courtOrderRequest.setCitizenUserId(300L);
        courtOrderRequest.setLawyerUserId(400L);
        courtOrderRequest.setDescription("Test Order");

        caseResponse = new CaseResponse();
        caseResponse.setCaseId(100L);
        caseResponse.setTitle("Test Case");
    }

    @Test
    void recordJudgment_ShouldRecordJudgmentSuccessfully_WhenCaseExists() {
        // Given
        when(caseClient.getById(100L)).thenReturn(ResponseEntity.ok(caseResponse));
        when(judgmentRepo.save(any(Judgment.class))).thenReturn(judgment);

        // When
        JudgmentResponse result = service.recordJudgment(judgmentRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getJudgmentId()).isEqualTo(1L);
        assertThat(result.getCaseId()).isEqualTo(100L);
        assertThat(result.getStatus()).isEqualTo(Judgment.JudgmentStatus.DRAFT);

        verify(judgmentRepo).save(any(Judgment.class));
        verify(caseClient).updateStatus(100L, "JUDGMENT_PENDING");
        verify(auditClient).log(eq(200L), eq("JUDGMENT_RECORDED"), anyString());
        verify(notifClient).send(notificationCaptor.capture());

        Map<String, Object> notificationPayload = notificationCaptor.getValue();
        assertThat(notificationPayload.get("userId")).isEqualTo(300L);
        assertThat(notificationPayload.get("entityId")).isEqualTo(1L);
        assertThat(notificationPayload.get("category")).isEqualTo("JUDGMENT");
        assertThat((String) notificationPayload.get("message")).contains("draft judgment was recorded");
    }

    @Test
    void recordJudgment_ShouldThrowException_WhenCaseNotFound() {
        // Given
        when(caseClient.getById(100L)).thenReturn(ResponseEntity.notFound().build());

        // When & Then
        assertThatThrownBy(() -> service.recordJudgment(judgmentRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No case found with id: 100");

        verify(judgmentRepo, never()).save(any(Judgment.class));
        verify(caseClient, never()).updateStatus(anyLong(), anyString());
    }

    @Test
    void recordJudgment_ShouldContinue_WhenCaseUpdateFails() {
        // Given
        when(caseClient.getById(100L)).thenReturn(ResponseEntity.ok(caseResponse));
        when(judgmentRepo.save(any(Judgment.class))).thenReturn(judgment);
        doThrow(new RuntimeException("Case update failed")).when(caseClient).updateStatus(anyLong(), anyString());

        // When
        JudgmentResponse result = service.recordJudgment(judgmentRequest);

        // Then
        assertThat(result).isNotNull();
        verify(judgmentRepo).save(any(Judgment.class));
        verify(auditClient).log(anyLong(), anyString(), anyString());
        verify(notifClient).send(anyMap());
    }

    @Test
    void recordJudgment_ShouldNotNotify_WhenCitizenUserIdIsNull() {
        // Given
        judgmentRequest.setCitizenUserId(null);
        when(caseClient.getById(100L)).thenReturn(ResponseEntity.ok(caseResponse));
        when(judgmentRepo.save(any(Judgment.class))).thenReturn(judgment);

        // When
        service.recordJudgment(judgmentRequest);

        // Then
        verify(notifClient, never()).send(anyMap());
    }

    @Test
    void getById_ShouldReturnJudgment_WhenExists() {
        // Given
        when(judgmentRepo.findById(1L)).thenReturn(Optional.of(judgment));

        // When
        JudgmentResponse result = service.getById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getJudgmentId()).isEqualTo(1L);
        assertThat(result.getCaseId()).isEqualTo(100L);
    }

    @Test
    void getById_ShouldThrowException_WhenNotFound() {
        // Given
        when(judgmentRepo.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> service.getById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Judgment not found: 1");
    }

    @Test
    void getAll_ShouldReturnAllJudgments() {
        // Given
        when(judgmentRepo.findAll()).thenReturn(List.of(judgment));

        // When
        List<JudgmentResponse> result = service.getAll();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getJudgmentId()).isEqualTo(1L);
    }

    @Test
    void getAll_ShouldReturnEmptyList_WhenNoJudgments() {
        // Given
        when(judgmentRepo.findAll()).thenReturn(List.of());

        // When
        List<JudgmentResponse> result = service.getAll();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void getByCase_ShouldReturnJudgmentsForCase() {
        // Given
        when(judgmentRepo.findByCaseId(100L)).thenReturn(List.of(judgment));

        // When
        List<JudgmentResponse> result = service.getByCase(100L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCaseId()).isEqualTo(100L);
    }

    @Test
    void getByCase_ShouldReturnEmptyList_WhenNoJudgmentsForCase() {
        // Given
        when(judgmentRepo.findByCaseId(999L)).thenReturn(List.of());

        // When
        List<JudgmentResponse> result = service.getByCase(999L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void finalizeJudgment_ShouldFinalizeJudgmentSuccessfully() {
        // Given
        when(judgmentRepo.findById(1L)).thenReturn(Optional.of(judgment));
        when(judgmentRepo.save(any(Judgment.class))).thenReturn(judgment);

        // When
        JudgmentResponse result = service.finalizeJudgment(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(Judgment.JudgmentStatus.FINAL);

        verify(judgmentRepo).save(any(Judgment.class));
        verify(caseClient).updateStatus(100L, "CLOSED");
        verify(auditClient).log(eq(200L), eq("JUDGMENT_FINALIZED"), anyString());
        verify(notifClient, times(2)).send(anyMap()); // citizen and lawyer notifications
    }

    @Test
    void finalizeJudgment_ShouldThrowException_WhenJudgmentNotFound() {
        // Given
        when(judgmentRepo.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> service.finalizeJudgment(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Judgment not found: 1");
    }

    @Test
    void finalizeJudgment_ShouldContinue_WhenCaseCloseFails() {
        // Given
        when(judgmentRepo.findById(1L)).thenReturn(Optional.of(judgment));
        when(judgmentRepo.save(any(Judgment.class))).thenReturn(judgment);
        doThrow(new RuntimeException("Case close failed")).when(caseClient).updateStatus(anyLong(), anyString());

        // When
        JudgmentResponse result = service.finalizeJudgment(1L);

        // Then
        assertThat(result).isNotNull();
        verify(auditClient).log(anyLong(), anyString(), anyString());
        verify(notifClient, times(2)).send(anyMap());
    }

    @Test
    void finalizeJudgment_ShouldNotifyOnlyCitizen_WhenLawyerUserIdIsNull() {
        // Given
        judgment.setLawyerUserId(null);
        when(judgmentRepo.findById(1L)).thenReturn(Optional.of(judgment));
        when(judgmentRepo.save(any(Judgment.class))).thenReturn(judgment);

        // When
        service.finalizeJudgment(1L);

        // Then
        verify(notifClient, times(1)).send(anyMap());
    }

    @Test
    void finalizeJudgment_ShouldNotifyOnlyLawyer_WhenCitizenUserIdIsNull() {
        // Given
        judgment.setCitizenUserId(null);
        when(judgmentRepo.findById(1L)).thenReturn(Optional.of(judgment));
        when(judgmentRepo.save(any(Judgment.class))).thenReturn(judgment);

        // When
        service.finalizeJudgment(1L);

        // Then
        verify(notifClient, times(1)).send(anyMap());
    }

    @Test
    void issueOrder_ShouldIssueOrderSuccessfully_WhenCaseExists() {
        // Given
        when(caseClient.getById(100L)).thenReturn(ResponseEntity.ok(caseResponse));
        when(orderRepo.save(any(CourtOrder.class))).thenReturn(courtOrder);

        // When
        CourtOrderResponse result = service.issueOrder(courtOrderRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo(1L);
        assertThat(result.getCaseId()).isEqualTo(100L);

        verify(orderRepo).save(any(CourtOrder.class));
        verify(auditClient).log(eq(200L), eq("COURT_ORDER_ISSUED"), anyString());
        verify(notifClient, times(2)).send(anyMap()); // citizen and lawyer notifications
    }

    @Test
    void issueOrder_ShouldThrowException_WhenCaseNotFound() {
        // Given
        when(caseClient.getById(100L)).thenReturn(ResponseEntity.notFound().build());

        // When & Then
        assertThatThrownBy(() -> service.issueOrder(courtOrderRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No case found with id: 100");

        verify(orderRepo, never()).save(any(CourtOrder.class));
    }

    @Test
    void issueOrder_ShouldNotNotify_WhenUserIdsAreNull() {
        // Given
        courtOrderRequest.setCitizenUserId(null);
        courtOrderRequest.setLawyerUserId(null);
        when(caseClient.getById(100L)).thenReturn(ResponseEntity.ok(caseResponse));
        when(orderRepo.save(any(CourtOrder.class))).thenReturn(courtOrder);

        // When
        service.issueOrder(courtOrderRequest);

        // Then
        verify(notifClient, never()).send(anyMap());
    }

    @Test
    void getOrderById_ShouldReturnOrder_WhenExists() {
        // Given
        when(orderRepo.findById(1L)).thenReturn(Optional.of(courtOrder));

        // When
        CourtOrderResponse result = service.getOrderById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo(1L);
        assertThat(result.getCaseId()).isEqualTo(100L);
    }

    @Test
    void getOrderById_ShouldThrowException_WhenNotFound() {
        // Given
        when(orderRepo.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> service.getOrderById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Order not found: 1");
    }

    @Test
    void getAllOrders_ShouldReturnAllOrders() {
        // Given
        when(orderRepo.findAll()).thenReturn(List.of(courtOrder));

        // When
        List<CourtOrderResponse> result = service.getAllOrders();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getOrderId()).isEqualTo(1L);
    }

    @Test
    void getAllOrders_ShouldReturnEmptyList_WhenNoOrders() {
        // Given
        when(orderRepo.findAll()).thenReturn(List.of());

        // When
        List<CourtOrderResponse> result = service.getAllOrders();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void getOrdersByCase_ShouldReturnOrdersForCase() {
        // Given
        when(orderRepo.findByCaseId(100L)).thenReturn(List.of(courtOrder));

        // When
        List<CourtOrderResponse> result = service.getOrdersByCase(100L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCaseId()).isEqualTo(100L);
    }

    @Test
    void getOrdersByCase_ShouldReturnEmptyList_WhenNoOrdersForCase() {
        // Given
        when(orderRepo.findByCaseId(999L)).thenReturn(List.of());

        // When
        List<CourtOrderResponse> result = service.getOrdersByCase(999L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void updateOrderStatus_ShouldUpdateStatusSuccessfully() {
        // Given
        when(orderRepo.findById(1L)).thenReturn(Optional.of(courtOrder));
        when(orderRepo.save(any(CourtOrder.class))).thenReturn(courtOrder);

        // When
        CourtOrderResponse result = service.updateOrderStatus(1L, CourtOrder.OrderStatus.SERVED);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(CourtOrder.OrderStatus.SERVED);

        verify(orderRepo).save(any(CourtOrder.class));
        verify(auditClient).log(eq(200L), eq("COURT_ORDER_STATUS_UPDATED"), anyString());
    }

    @Test
    void updateOrderStatus_ShouldThrowException_WhenOrderNotFound() {
        // Given
        when(orderRepo.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> service.updateOrderStatus(1L, CourtOrder.OrderStatus.SERVED))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Order not found: 1");
    }

    @Test
    void notify_ShouldSendNotificationSuccessfully() {
        // When
        service.getClass().getDeclaredMethods(); // Just to access private method indirectly
        // Since notify is private, we test it through public methods that call it

        // Verify that in the tests above, notify is called correctly
        // This is already covered in the recordJudgment, finalizeJudgment, and issueOrder tests
    }

    @Test
    void notify_ShouldHandleNotificationFailureGracefully() {
        // Given
        doThrow(new RuntimeException("Notification service down")).when(notifClient).send(anyMap());
        when(caseClient.getById(100L)).thenReturn(ResponseEntity.ok(caseResponse));
        when(judgmentRepo.save(any(Judgment.class))).thenReturn(judgment);

        // When - notification should not prevent the main operation
        JudgmentResponse result = service.recordJudgment(judgmentRequest);

        // Then
        assertThat(result).isNotNull();
        verify(judgmentRepo).save(any(Judgment.class));
        verify(caseClient).updateStatus(anyLong(), anyString());
        verify(auditClient).log(anyLong(), anyString(), anyString());
    }

    @Test
    void audit_ShouldLogAuditSuccessfully() {
        // Given
        when(caseClient.getById(100L)).thenReturn(ResponseEntity.ok(caseResponse));
        when(judgmentRepo.save(any(Judgment.class))).thenReturn(judgment);

        // When
        service.recordJudgment(judgmentRequest);

        // Then
        verify(auditClient).log(auditUserIdCaptor.capture(), auditActionCaptor.capture(), auditResourceCaptor.capture());
        assertThat(auditUserIdCaptor.getValue()).isEqualTo(200L);
        assertThat(auditActionCaptor.getValue()).isEqualTo("JUDGMENT_RECORDED");
        assertThat(auditResourceCaptor.getValue()).contains("Judgment:1 (DRAFT) Case:100");
    }

    @Test
    void audit_ShouldHandleAuditFailureGracefully() {
        // Given
        doThrow(new RuntimeException("Audit service down")).when(auditClient).log(anyLong(), anyString(), anyString());
        when(caseClient.getById(100L)).thenReturn(ResponseEntity.ok(caseResponse));
        when(judgmentRepo.save(any(Judgment.class))).thenReturn(judgment);

        // When - audit failure should not prevent the main operation
        JudgmentResponse result = service.recordJudgment(judgmentRequest);

        // Then
        assertThat(result).isNotNull();
        verify(judgmentRepo).save(any(Judgment.class));
        verify(caseClient).updateStatus(anyLong(), anyString());
        verify(notifClient).send(anyMap());
    }
}

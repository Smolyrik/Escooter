package com.escooter.controller;

import com.escooter.dto.PaymentDto;
import com.escooter.dto.PaymentStatusDto;
import com.escooter.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private PaymentDto paymentDto;
    private UUID paymentId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        paymentId = UUID.randomUUID();
        userId = UUID.randomUUID();
        paymentDto = PaymentDto.builder()
                .id(paymentId)
                .userId(userId)
                .amount(new BigDecimal("50.00"))
                .paymentTime(java.time.LocalDateTime.now())
                .statusId(1)
                .build();
    }

    @Test
    void makePayment_ShouldReturnPayment() {
        when(paymentService.makePayment(eq(userId), any(BigDecimal.class))).thenReturn(paymentDto);
        ResponseEntity<PaymentDto> response = paymentController.makePayment(userId, new BigDecimal("50.00"));

        assertNotNull(response.getBody());
        assertEquals(paymentDto.getId(), response.getBody().getId());
        verify(paymentService, times(1)).makePayment(eq(userId), any(BigDecimal.class));
    }

    @Test
    void getPaymentById_ShouldReturnPayment() {
        when(paymentService.getPaymentById(paymentId)).thenReturn(paymentDto);
        ResponseEntity<PaymentDto> response = paymentController.getPaymentById(paymentId);

        assertNotNull(response.getBody());
        assertEquals(paymentId, response.getBody().getId());
        verify(paymentService, times(1)).getPaymentById(paymentId);
    }

    @Test
    void getUserPayments_ShouldReturnPaymentList() {
        when(paymentService.getUserPayments(userId)).thenReturn(Collections.singletonList(paymentDto));
        ResponseEntity<List<PaymentDto>> response = paymentController.getUserPayments(userId);

        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        verify(paymentService, times(1)).getUserPayments(userId);
    }

    @Test
    void updatePaymentStatus_ShouldReturnUpdatedPayment() {
        PaymentStatusDto statusDto = new PaymentStatusDto(2, "Pending");
        when(paymentService.updatePaymentStatus(eq(paymentId), any(PaymentStatusDto.class))).thenReturn(paymentDto);
        ResponseEntity<PaymentDto> response = paymentController.updatePaymentStatus(paymentId, statusDto);

        assertNotNull(response.getBody());
        assertEquals(paymentId, response.getBody().getId());
        verify(paymentService, times(1)).updatePaymentStatus(eq(paymentId), any(PaymentStatusDto.class));
    }
}

package com.escooter.service;

import com.escooter.dto.PaymentDto;
import com.escooter.dto.PaymentStatusDto;
import com.escooter.entity.Payment;
import com.escooter.entity.PaymentStatus;
import com.escooter.entity.User;
import com.escooter.mapper.PaymentMapper;
import com.escooter.repository.PaymentRepository;
import com.escooter.repository.PaymentStatusRepository;
import com.escooter.repository.UserRepository;
import com.escooter.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentStatusRepository paymentStatusRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Payment payment;
    private PaymentDto paymentDto;
    private UUID paymentId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        paymentId = UUID.randomUUID();
        userId = UUID.randomUUID();
        User user = User.builder().id(userId).build();
        PaymentStatus status = PaymentStatus.builder().id(1).name("Pending").build();

        payment = Payment.builder()
                .id(paymentId)
                .user(user)
                .amount(new BigDecimal("50.00"))
                .paymentTime(LocalDateTime.now())
                .status(status)
                .build();

        paymentDto = PaymentDto.builder()
                .id(paymentId)
                .userId(userId)
                .amount(new BigDecimal("50.00"))
                .paymentTime(LocalDateTime.now())
                .statusId(1)
                .build();
    }

    @Test
    void makePayment_ShouldReturnPaymentDto() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(payment.getUser()));
        when(paymentStatusRepository.findByName("PENDING")).thenReturn(Optional.of(payment.getStatus()));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        PaymentDto result = paymentService.makePayment(userId, new BigDecimal("50.00"));

        assertNotNull(result);
        assertEquals(paymentId, result.getId());
        assertEquals(userId, result.getUserId());
        assertEquals(new BigDecimal("50.00"), result.getAmount());

        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void getPaymentById_ShouldReturnPaymentDto_WhenPaymentExists() {
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        PaymentDto result = paymentService.getPaymentById(paymentId);

        assertNotNull(result);
        assertEquals(paymentId, result.getId());

        verify(paymentRepository, times(1)).findById(paymentId);
    }

    @Test
    void getPaymentById_ShouldThrowException_WhenPaymentNotFound() {
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> paymentService.getPaymentById(paymentId));

        verify(paymentRepository, times(1)).findById(paymentId);
    }

    @Test
    void getUserPayments_ShouldReturnPaymentDtoList() {
        when(paymentRepository.findByUserId(userId)).thenReturn(List.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        List<PaymentDto> result = paymentService.getUserPayments(userId);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(paymentRepository, times(1)).findByUserId(userId);
    }

    @Test
    void updatePaymentStatus_ShouldReturnUpdatedPaymentDto() {
        PaymentStatusDto newStatusDto = new PaymentStatusDto(2, "Completed");
        PaymentStatus newStatus = PaymentStatus.builder().id(2).name("Completed").build();

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(paymentStatusRepository.findByName("Completed")).thenReturn(Optional.of(newStatus));
        when(paymentRepository.save(payment)).thenReturn(payment);

        PaymentDto updatedPaymentDto = PaymentDto.builder()
                .id(paymentId)
                .userId(userId)
                .amount(payment.getAmount())
                .paymentTime(payment.getPaymentTime())
                .statusId(2)
                .build();

        when(paymentMapper.toDto(payment)).thenReturn(updatedPaymentDto);

        PaymentDto result = paymentService.updatePaymentStatus(paymentId, newStatusDto);

        assertNotNull(result);
        assertEquals(2, result.getStatusId());

        verify(paymentRepository, times(1)).save(payment);
        verify(paymentMapper, times(1)).toDto(payment);
    }

    @Test
    void updatePaymentStatus_ShouldThrowException_WhenPaymentNotFound() {
        PaymentStatusDto newStatusDto = new PaymentStatusDto(2, "Completed");
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> paymentService.updatePaymentStatus(paymentId, newStatusDto));
    }
}

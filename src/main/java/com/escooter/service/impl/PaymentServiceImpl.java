package com.escooter.service.impl;

import com.escooter.dto.PaymentDto;
import com.escooter.dto.PaymentStatusDto;
import com.escooter.entity.Payment;
import com.escooter.entity.PaymentStatus;
import com.escooter.entity.User;
import com.escooter.mapper.PaymentMapper;
import com.escooter.repository.PaymentRepository;
import com.escooter.repository.PaymentStatusRepository;
import com.escooter.repository.UserRepository;
import com.escooter.service.PaymentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentStatusRepository paymentStatusRepository;
    private final UserRepository userRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public PaymentDto makePayment(UUID userId, BigDecimal amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User with ID: {} not found", userId);
                    return new NoSuchElementException("User with ID: " + userId + " not found");
                });

        PaymentStatus status = paymentStatusRepository.findByName("COMPLETED")
                .orElseThrow(() -> {
                    log.error("Payment status not found");
                    return new NoSuchElementException("Payment status not found");
                });

        Payment payment = Payment.builder()
                .user(user)
                .amount(amount)
                .paymentTime(LocalDateTime.now())
                .status(status)
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        user.setBalance(user.getBalance().add(amount));
        userRepository.save(user);
        log.info("Created new payment with ID: {}", savedPayment.getId());

        return paymentMapper.toDto(savedPayment);
    }

    @Override
    public PaymentDto getPaymentById(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .map(paymentMapper::toDto)
                .orElseThrow(() -> {
                    log.error("Payment with ID: {} not found", paymentId);
                    return new NoSuchElementException("Payment with ID: " + paymentId + " not found");
                });
    }

    @Override
    public List<PaymentDto> getUserPayments(UUID userId) {
        List<Payment> payments = paymentRepository.findByUserId(userId);
        log.info("Fetching payments for user with ID: {}", userId);
        return payments.stream()
                .map(paymentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentDto updatePaymentStatus(UUID paymentId, PaymentStatusDto status) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> {
                    log.error("Payment with ID: {} not found", paymentId);
                    return new NoSuchElementException("Payment with ID: " + paymentId + " not found");
                });

        PaymentStatus newStatus = paymentStatusRepository.findByName(status.getName())
                .orElseThrow(() -> {
                    log.error("Payment status with Name: {} not found", status.getName());
                    return new NoSuchElementException("Payment status with Name: " + status.getName() + " not found");
                });

        payment.setStatus(newStatus);

        Payment updatedPayment = paymentRepository.save(payment);
        log.info("Updated payment with ID: {}", updatedPayment.getId());

        return paymentMapper.toDto(updatedPayment);
    }
}

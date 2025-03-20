package com.escooter.service;

import com.escooter.dto.PaymentDto;
import com.escooter.dto.PaymentStatusDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface PaymentService {

    PaymentDto makePayment(UUID userId, BigDecimal amount);

    PaymentDto getPaymentById(UUID paymentId);

    List<PaymentDto> getUserPayments(UUID userId);

    PaymentDto updatePaymentStatus(UUID paymentId, PaymentStatusDto status);
}

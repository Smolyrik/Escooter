package com.escooter.controller;

import com.escooter.dto.PaymentDto;
import com.escooter.dto.PaymentStatusDto;
import com.escooter.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/make")
    public ResponseEntity<PaymentDto> makePayment(@RequestParam UUID userId, @RequestParam BigDecimal amount) {
        PaymentDto payment = paymentService.makePayment(userId, amount);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDto> getPaymentById(@PathVariable UUID paymentId) {
        PaymentDto payment = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentDto>> getUserPayments(@PathVariable UUID userId) {
        List<PaymentDto> payments = paymentService.getUserPayments(userId);
        return ResponseEntity.ok(payments);
    }

    @PatchMapping("/{paymentId}/status")
    public ResponseEntity<PaymentDto> updatePaymentStatus(@PathVariable UUID paymentId, @RequestBody PaymentStatusDto statusDto) {
        PaymentDto updatedPayment = paymentService.updatePaymentStatus(paymentId, statusDto);
        return ResponseEntity.ok(updatedPayment);
    }
}

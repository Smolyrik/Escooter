package com.escooter.controller;

import com.escooter.dto.PaymentDto;
import com.escooter.dto.PaymentStatusDto;
import com.escooter.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payment Management", description = "Operations related to payments")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(
            summary = "Make a payment",
            description = "Creates a new payment for a user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully made the payment",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PaymentDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            })
    @PostMapping("/make")
    public ResponseEntity<PaymentDto> makePayment(
            @Parameter(description = "User ID")
            @RequestParam @NotNull(message = "User ID cannot be null") UUID userId,

            @Parameter(description = "Payment amount")
            @RequestParam @NotNull(message = "Amount cannot be null")
            @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
            BigDecimal amount) {

        PaymentDto payment = paymentService.makePayment(userId, amount);
        return ResponseEntity.ok(payment);
    }

    @Operation(
            summary = "Get payment by ID",
            description = "Fetches payment details by payment ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully fetched the payment",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PaymentDto.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "Payment not found")
            })
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDto> getPaymentById(
            @Parameter(description = "Payment ID")
            @NotNull(message = "Payment ID cannot be null") @PathVariable UUID paymentId) {
        PaymentDto payment = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(payment);
    }

    @Operation(
            summary = "Get user payments",
            description = "Fetches all payments for a specific user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully fetched the payments",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PaymentDto.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentDto>> getUserPayments(
            @Parameter(description = "User ID")
            @NotNull(message = "User ID cannot be null") @PathVariable UUID userId) {
        List<PaymentDto> payments = paymentService.getUserPayments(userId);
        return ResponseEntity.ok(payments);
    }

    @Operation(
            summary = "Update payment status",
            description = "Updates the status of a payment.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully updated the payment status",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PaymentDto.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "Payment not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            })
    @PatchMapping("/{paymentId}/status")
    public ResponseEntity<PaymentDto> updatePaymentStatus(
            @Parameter(description = "Payment ID")
            @NotNull(message = "Payment ID cannot be null") @PathVariable UUID paymentId,

            @Valid @RequestBody PaymentStatusDto statusDto) {
        PaymentDto updatedPayment = paymentService.updatePaymentStatus(paymentId, statusDto);
        return ResponseEntity.ok(updatedPayment);
    }
}

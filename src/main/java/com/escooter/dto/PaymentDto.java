package com.escooter.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {

    private UUID id;

    @NotNull(message = "User id must not be null")
    private UUID userId;

    @NotNull(message = "Payment amount must not be null")
    @Min(0)
    private BigDecimal amount;

    @NotNull(message = "Payment time must not be null")
    private LocalDateTime paymentTime;

    @NotNull(message = "Status id must not be null")
    private Integer statusId;
}

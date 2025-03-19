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
public class RentalDto {

    private UUID id;

    @NotNull(message = "User id must not be null")
    private UUID userId;

    @NotNull(message = "Scooter id must not be null")
    private UUID scooterId;

    @NotNull(message = "Status id must not be null")
    private Integer statusId;

    @NotNull(message = "Rental type id must not be null")
    private Integer rentalTypeId;

    @NotNull(message = "Start time must not be null")
    private LocalDateTime startTime;

    @NotNull(message = "End time must not be null")
    private LocalDateTime endTime;

    @NotNull(message = "Final price must not be null")
    @Min(0)
    private BigDecimal totalPrice;

    @NotNull(message = "Distance must be not null")
    private BigDecimal distance;
}

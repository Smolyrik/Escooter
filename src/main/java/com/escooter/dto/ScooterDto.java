package com.escooter.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScooterDto {

    private UUID id;

    @NotNull(message = "Rental point id must not be null")
    private UUID rentalPointId;

    @NotNull(message = "Model id must not be null")
    private Integer modelId;

    @NotNull(message = "Battery level must not be null")
    @Min(0)
    @Max(100)
    private BigDecimal batteryLevel;

    @NotNull(message = "Status id must not be null")
    private Integer statusId;

    @NotNull(message = "Mileage must not be null")
    @Min(0)
    private BigDecimal mileage;
}

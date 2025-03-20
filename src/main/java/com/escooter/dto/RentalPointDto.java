package com.escooter.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class RentalPointDto {

    private UUID id;

    @NotNull(message = "Rental point name must not be null")
    @Size(max = 50, message = "Rental point name must not exceed 50 characters")
    private String name;

    @NotNull(message = "Latitude must not be null")
    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
    private BigDecimal latitude;

    @NotNull(message = "Longitude must not be null")
    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
    private BigDecimal longitude;

    @NotNull(message = "Address must not be null")
    @Size(max = 256, message = "Address must not exceed 256 characters")
    private String address;

    @NotNull(message = "Manager id must not be null")
    private UUID managerId;
}

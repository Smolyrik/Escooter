package com.escooter.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class PricingPlanDto {

    private UUID id;

    @NotNull(message = "Pricing plan name must not be null")
    @Size(min = 4, max = 50, message = "Pricing plan name must be between 4 and 50 characters")
    private String name;

    @NotNull(message = "Price per hour must not be null")
    @Min(0)
    private BigDecimal pricePerHour;

    @NotNull(message = "Subscription must not be null")
    @Min(0)
    private BigDecimal subscriptionPrice;

    @NotNull(message = "Discount must not be null")
    @Min(0)
    @Max(100)
    private BigDecimal discount;
}

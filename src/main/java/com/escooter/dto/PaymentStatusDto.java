package com.escooter.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStatusDto {

    private Integer id;

    @NotNull(message = "Payment status name must not be null")
    @Size(min = 4, max = 50, message = "Payment status name must be between 4 and 50 characters")
    private String name;
}

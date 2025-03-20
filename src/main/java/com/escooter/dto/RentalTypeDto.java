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
public class RentalTypeDto {

    private Integer id;

    @NotNull(message = "Rental type name must not be null")
    @Size(min = 6, max = 50, message = "Rental type name must be between 6 and 50 characters")
    private String name;
}

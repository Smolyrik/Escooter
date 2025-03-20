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
public class ModelDto {

    private Integer id;

    @NotNull(message = "Model name must not be null")
    @Size(min = 4, max = 50, message = "Model name must be between 4 and 50 characters")
    private String name;
}

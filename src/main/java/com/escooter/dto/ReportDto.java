package com.escooter.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDto {

    private UUID id;

    @NotNull(message = "Report type must not be null")
    @Size(min = 4, max = 50, message = "Report type must be between 4 and 50 characters")
    private String reportType;

    @NotNull(message = "Creation time must not be null")
    @PastOrPresent(message = "Creation date cannot be in the future")
    private LocalDateTime createdAt;

    @NotNull(message = "Report data must not be null")
    @Size(min = 10, message = "Report data must be greater than 10 characters")
    private String data;
}

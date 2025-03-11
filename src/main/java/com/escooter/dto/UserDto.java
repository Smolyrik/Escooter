package com.escooter.dto;

import jakarta.validation.constraints.*;
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
public class UserDto {

    private UUID id;

    @NotNull(message = "Role id must not be null")
    private Integer roleId;

    @NotNull(message = "User name must not be null")
    private String name;

    @NotNull(message = "Email must not be null")
    @Email(message = "Email must be a valid email address")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @NotNull(message = "Phone number cannot be null")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$",
            message = "Phone number must be in a valid format (e.g., +1234567890)")
    private String phone;

    @NotNull(message = "Password must not be null")
    private String passwordHash;

    @NotNull(message = "Balance must not be null")
    @Min(0)
    private BigDecimal balance;

}

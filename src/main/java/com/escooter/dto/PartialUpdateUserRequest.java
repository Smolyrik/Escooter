package com.escooter.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PartialUpdateUserRequest {

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
}

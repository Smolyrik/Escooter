package com.escooter.security;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SignUpRequest {

    @Size(min = 5, max = 50, message = "User name must be between 5 and 50 characters")
    @NotBlank(message = "User name cannot be empty")
    private String name;

    @Size(min = 5, max = 255, message = "Email address must be between 5 and 255 characters")
    @NotBlank(message = "Email address cannot be empty")
    @Email(message = "Email must be in the format user@example.com")
    private String email;

    @NotNull(message = "Phone number cannot be null")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$",
            message = "Phone number must be in a valid format (e.g., +1234567890)")
    private String phone;

    @Size(min = 8, max = 255, message = "Password length must be between 8 and 255 characters")
    @Size(max = 255, message = "Password length must be no more than 255 characters")
    private String password;
}

package com.escooter.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @Size(max = 255, message = "Password length must be no more than 255 characters")
    private String password;
}

package com.escooter.security;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user authentication")
public class AuthController {
    private final AuthenticationService authenticationService;

    @Operation(summary = "User Sign-Up", description = "Registers a new user and returns a JWT token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully registered and authenticated"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping("/sign-up")
    public JwtAuthenticationResponse signUp(@RequestBody @Valid SignUpRequest request) {
        return authenticationService.signUp(request);
    }

    @Operation(summary = "User Sign-In", description = "Authenticates a user and returns a JWT token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/sign-in")
    public JwtAuthenticationResponse signIn(@RequestBody @Valid SignInRequest request) {
        return authenticationService.signIn(request);
    }
}
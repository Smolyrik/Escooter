package com.escooter.controller;

import com.escooter.dto.UserDto;
import com.escooter.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "User Management", description = "Operations related to user management")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Add a new user",
            description = "Creates a new user. Only users with the MANAGER role can perform this action.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully created user",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserDto.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data")
            })
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<UserDto> addUser(@Valid @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.addUser(userDto));
    }

    @Operation(
            summary = "Get user by ID",
            description = "Fetches user details by ID. The user can only access their own data unless they have the MANAGER role.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved user",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserDto.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            })
    @GetMapping("/{userId}")
    @PreAuthorize("#userId == authentication.name or hasRole('MANAGER')")
    public ResponseEntity<UserDto> getUserById(
            @Parameter(description = "User ID") @PathVariable UUID userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @Operation(
            summary = "Get all users",
            description = "Fetches a list of all users. Only users with the MANAGER role can access this.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved user list",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserDto.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            })
    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(
            summary = "Update user details",
            description = "Updates user information. A user can only update their own data unless they have the MANAGER role.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully updated user",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserDto.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            })
    @PutMapping("/{userId}")
    @PreAuthorize("#userId == authentication.name or hasRole('MANAGER')")
    public ResponseEntity<UserDto> updateUser(
            @Parameter(description = "User ID") @PathVariable UUID userId,
            @Valid @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.updateUser(userId, userDto));
    }

    @Operation(
            summary = "Delete a user",
            description = "Deletes a user by ID. A user can only delete their own account unless they have the MANAGER role.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully deleted user"),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            })
    @DeleteMapping("/{userId}")
    @PreAuthorize("#userId == authentication.name or hasRole('MANAGER')")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID") @PathVariable UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}

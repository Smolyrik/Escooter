package com.escooter.controller;

import com.escooter.dto.RentalDto;
import com.escooter.service.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
@Tag(name = "Rental Management", description = "Operations related to scooter rentals")
@SecurityRequirement(name = "bearerAuth")
public class RentalController {

    private final RentalService rentalService;

    @Operation(
            summary = "Start a scooter rental",
            description = "Begins a rental for a scooter.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully started rental",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = RentalDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            })
    @PostMapping("/start")
    public ResponseEntity<RentalDto> rentScooter(
            @Parameter(description = "User ID")
            @RequestParam @NotNull(message = "User ID cannot be null") UUID userId,

            @Parameter(description = "Scooter ID")
            @RequestParam @NotNull(message = "Scooter ID cannot be null") UUID scooterId,

            @Parameter(description = "Rental type ID")
            @RequestParam @NotNull(message = "Rental type cannot be null") Integer rentalTypeId
            ) {

        return ResponseEntity.ok(rentalService.rentScooter(userId, scooterId, rentalTypeId));
    }


    @Operation(
            summary = "Get all rentals",
            description = "Fetches all scooter rentals.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully fetched all rentals",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = RentalDto.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            })
    @GetMapping
    public ResponseEntity<List<RentalDto>> getAllRentals() {
        return ResponseEntity.ok(rentalService.getAllRentals());
    }

    @Operation(
            summary = "End a rental",
            description = "Ends an ongoing rental.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully ended rental",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = RentalDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "Rental not found")
            })
    @PostMapping("/end")
    public ResponseEntity<RentalDto> endRental(
            @Parameter(description = "Rental ID")
            @NotNull(message = "Rental ID cannot be null") @RequestParam UUID rentalId,

            @Parameter(description = "Rental distance")
            @DecimalMin(value = "0.01", message = "Distance must be at least 0.01km")
            @NotNull(message = "Rental distance cannot be null") @RequestParam BigDecimal distance
            ) {
        return ResponseEntity.ok(rentalService.endRental(rentalId, distance));
    }

    @Operation(
            summary = "Get rentals by user ID",
            description = "Fetches all rentals associated with a specific user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully fetched user rentals",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = RentalDto.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RentalDto>> getRentalsByUserId(
            @Parameter(description = "User ID")
            @NotNull(message = "User ID cannot be null") @PathVariable UUID userId) {
        return ResponseEntity.ok(rentalService.getRentalsByUserId(userId));
    }

    @Operation(
            summary = "Get rentals by scooter ID",
            description = "Fetches all rentals associated with a specific scooter.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully fetched scooter rentals",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = RentalDto.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "Scooter not found")
            })
    @GetMapping("/scooter/{scooterId}")
    public ResponseEntity<List<RentalDto>> getRentalsByScooterId(
            @Parameter(description = "Scooter ID")
            @NotNull(message = "Scooter ID cannot be null") @PathVariable UUID scooterId) {
        return ResponseEntity.ok(rentalService.getRentalsByScooterId(scooterId));
    }
}

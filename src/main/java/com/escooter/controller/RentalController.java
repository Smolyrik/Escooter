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
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @Parameter(description = "User ID") @RequestParam UUID userId,
            @Parameter(description = "Scooter ID") @RequestParam UUID scooterId,
            @Parameter(description = "Rental Type ID") @RequestParam Integer rentalTypeId) {
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
            @Parameter(description = "Rental ID") @RequestParam UUID rentalId) {
        return ResponseEntity.ok(rentalService.endRental(rentalId));
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
            @Parameter(description = "User ID") @PathVariable UUID userId) {
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
            @Parameter(description = "Scooter ID") @PathVariable UUID scooterId) {
        return ResponseEntity.ok(rentalService.getRentalsByScooterId(scooterId));
    }
}

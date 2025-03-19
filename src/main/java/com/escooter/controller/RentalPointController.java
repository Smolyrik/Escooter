package com.escooter.controller;

import com.escooter.dto.RentalPointDto;
import com.escooter.dto.ScooterDto;
import com.escooter.service.RentalPointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/rental-points")
@RequiredArgsConstructor
@Tag(name = "Rental Point Management", description = "Operations related to rental points and scooters")
@SecurityRequirement(name = "bearerAuth")
public class RentalPointController {

    private final RentalPointService rentalPointService;

    @Operation(
            summary = "Add a new rental point",
            description = "Creates a new rental point for scooters.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully added rental point",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RentalPointDto.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            })
    @PostMapping
    public ResponseEntity<RentalPointDto> addRentalPoint(@Valid @RequestBody RentalPointDto rentalPointDto) {
        return ResponseEntity.ok(rentalPointService.addRentalPoint(rentalPointDto));
    }

    @Operation(
            summary = "Get rental point by ID",
            description = "Fetches details of a rental point by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully fetched rental point",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RentalPointDto.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            })
    @GetMapping("/{id}")
    public ResponseEntity<RentalPointDto> getRentalPointById(
            @Parameter(description = "Rental point ID")
            @NotNull(message = "Rental point ID cannot be null") @PathVariable UUID id) {
        return ResponseEntity.ok(rentalPointService.getRentalPointById(id));
    }

    @Operation(
            summary = "Get all rental points",
            description = "Fetches a list of all rental points.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully fetched rental points",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RentalPointDto.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            })
    @GetMapping
    public ResponseEntity<List<RentalPointDto>> getAllRentalPoints() {
        return ResponseEntity.ok(rentalPointService.getAllRentalPoints());
    }

    @Operation(
            summary = "Get nearby rental points",
            description = "Fetches a list of nearby rental points based on user location.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully fetched nearby rental points",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = RentalPointDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            })
    @GetMapping("/nearby")
    public ResponseEntity<List<RentalPointDto>> getNearbyRentalPoints(
            @Parameter(description = "User latitude")
            @RequestParam @NotNull @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
            @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
            BigDecimal latitude,

            @Parameter(description = "User longitude")
            @RequestParam @NotNull @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
            @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
            BigDecimal longitude) {

        return ResponseEntity.ok(rentalPointService.getNearbyRentalPoints(latitude, longitude));
    }

    @Operation(
            summary = "Update rental point",
            description = "Updates the details of an existing rental point.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully updated rental point",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RentalPointDto.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            })
    @PutMapping("/{id}")
    public ResponseEntity<RentalPointDto> updateRentalPoint(
            @Parameter(description = "Rental point ID")
            @NotNull(message = "Rental point ID cannot be null") @PathVariable UUID id,
            @Valid @RequestBody RentalPointDto rentalPointDto) {
        return ResponseEntity.ok(rentalPointService.updateRentalPoint(id, rentalPointDto));
    }

    @Operation(
            summary = "Delete rental point",
            description = "Deletes a rental point by its ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully deleted rental point"),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRentalPoint(
            @Parameter(description = "Rental point ID")
            @NotNull(message = "Rental point ID cannot be null") @PathVariable UUID id) {
        rentalPointService.deleteRentalPoint(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get all scooters at a rental point",
            description = "Fetches a list of all scooters located at a specific rental point.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully fetched scooters",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ScooterDto.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            })
    @GetMapping("/{rentalPointId}/scooters")
    public ResponseEntity<List<ScooterDto>> getAllScootersByRentalPoint(
            @Parameter(description = "Rental point ID")
            @NotNull(message = "Rental point ID cannot be null") @PathVariable UUID rentalPointId) {
        return ResponseEntity.ok(rentalPointService.getAllScootersByRentalPoint(rentalPointId));
    }

    @Operation(
            summary = "Get available scooters at a rental point",
            description = "Fetches a list of available scooters at a specific rental point.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully fetched available scooters",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ScooterDto.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            })
    @GetMapping("/{rentalPointId}/scooters/available")
    public ResponseEntity<List<ScooterDto>> getAvailableScootersByRentalPoint(
            @Parameter(description = "Rental point ID")
            @NotNull(message = "Rental point ID cannot be null") @PathVariable UUID rentalPointId) {
        return ResponseEntity.ok(rentalPointService.getAvailableScootersByRentalPoint(rentalPointId));
    }

    @Operation(
            summary = "Get rented scooters at a rental point",
            description = "Fetches a list of rented scooters at a specific rental point.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully fetched rented scooters",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ScooterDto.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            })
    @GetMapping("/{rentalPointId}/scooters/rented")
    public ResponseEntity<List<ScooterDto>> getRentedScootersByRentalPoint(
            @Parameter(description = "Rental point ID")
            @NotNull(message = "Rental point ID cannot be null") @PathVariable UUID rentalPointId) {
        return ResponseEntity.ok(rentalPointService.getRentedScootersByRentalPoint(rentalPointId));
    }

    @Operation(
            summary = "Get on repair scooters at a rental point",
            description = "Fetches a list of on repair scooters at a specific rental point.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully fetched on repair scooters",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ScooterDto.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            })
    @GetMapping("/{rentalPointId}/scooters/repair")
    public ResponseEntity<List<ScooterDto>> getScootersOnRepairByRentalPoint(
            @Parameter(description = "Rental point ID")
            @NotNull(message = "Rental point ID cannot be null") @PathVariable UUID rentalPointId) {
        return ResponseEntity.ok(rentalPointService.getScootersOnRepairByRentalPoint(rentalPointId));
    }
}

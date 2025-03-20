package com.escooter.controller;

import com.escooter.dto.PricingPlanDto;
import com.escooter.dto.ScooterDto;
import com.escooter.service.ScooterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/scooters")
@RequiredArgsConstructor
@Validated
@Tag(name = "Scooter Management", description = "Operations related to scooter management")
@SecurityRequirement(name = "bearerAuth")
public class ScooterController {

    private final ScooterService scooterService;

    @Operation(
            summary = "Add a new scooter",
            description = "Creates a new scooter in the system.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully created scooter",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ScooterDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            })
    @PostMapping
    public ResponseEntity<ScooterDto> addScooter(@Valid @RequestBody ScooterDto scooterDto) {
        return ResponseEntity.ok(scooterService.addScooter(scooterDto));
    }

    @Operation(
            summary = "Get scooter by ID",
            description = "Fetches details of a specific scooter by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved scooter details",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ScooterDto.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "Scooter not found")
            })
    @GetMapping("/{scooterId}")
    public ResponseEntity<ScooterDto> getScooterById(
            @Parameter(description = "Scooter ID")
            @NotNull(message = "Scooter ID cannot be null") @PathVariable UUID scooterId) {
        return ResponseEntity.ok(scooterService.getScooterById(scooterId));
    }

    @Operation(
            summary = "Get all scooters",
            description = "Retrieves a list of all scooters in the system.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of scooters",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ScooterDto.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            })
    @GetMapping
    public ResponseEntity<List<ScooterDto>> getAllScooters() {
        return ResponseEntity.ok(scooterService.getAllScooters());
    }

    @Operation(
            summary = "Update a scooter",
            description = "Updates an existing scooter identified by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully updated scooter",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ScooterDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "Scooter not found")
            })
    @PutMapping("/{scooterId}")
    public ResponseEntity<ScooterDto> updateScooter(
            @Parameter(description = "Scooter ID")
            @NotNull(message = "Scooter ID cannot be null") @PathVariable UUID scooterId,
            @Valid @RequestBody ScooterDto scooterDto) {
        return ResponseEntity.ok(scooterService.updateScooter(scooterId, scooterDto));
    }

    @Operation(
            summary = "Delete a scooter",
            description = "Deletes a scooter identified by its ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully deleted scooter"),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "Scooter not found")
            })
    @DeleteMapping("/{scooterId}")
    public ResponseEntity<Void> deleteScooter(
            @Parameter(description = "Scooter ID")
            @NotNull(message = "Scooter ID cannot be null") @PathVariable UUID scooterId) {
        scooterService.deleteScooter(scooterId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get pricing plan for a scooter",
            description = "Fetches the pricing plan associated with a specific scooter.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved pricing plan",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PricingPlanDto.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "Scooter or pricing plan not found")
            })
    @GetMapping("/{scooterId}/pricing-plan")
    public ResponseEntity<PricingPlanDto> getPricingPlanByScooterId(
            @Parameter(description = "Scooter ID")
            @NotNull(message = "Scooter ID cannot be null") @PathVariable UUID scooterId) {
        return ResponseEntity.ok(scooterService.getPricingPlanByScooterId(scooterId));
    }
}

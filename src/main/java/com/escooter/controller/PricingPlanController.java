package com.escooter.controller;

import com.escooter.dto.PricingPlanDto;
import com.escooter.service.PricingPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pricing-plans")
@RequiredArgsConstructor
@Tag(name = "Pricing Plan Management", description = "Operations related to pricing plans")
@SecurityRequirement(name = "bearerAuth")
public class PricingPlanController {

    private final PricingPlanService pricingPlanService;

    @Operation(
            summary = "Add a new pricing plan",
            description = "Creates a new pricing plan.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully added the pricing plan",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PricingPlanDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            })
    @PostMapping
    public ResponseEntity<PricingPlanDto> addPricingPlan(@Valid @RequestBody PricingPlanDto pricingPlanDto) {
        return ResponseEntity.ok(pricingPlanService.addPricingPlan(pricingPlanDto));
    }

    @Operation(
            summary = "Get pricing plan by ID",
            description = "Fetches pricing plan details by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully fetched the pricing plan",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PricingPlanDto.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "Pricing plan not found")
            })
    @GetMapping("/{id}")
    public ResponseEntity<PricingPlanDto> getPricingPlan(
            @Parameter(description = "Pricing Plan ID") @PathVariable UUID id) {
        return ResponseEntity.ok(pricingPlanService.getPricingPlan(id));
    }

    @Operation(
            summary = "Get all pricing plans",
            description = "Fetches all available pricing plans.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully fetched the pricing plans",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PricingPlanDto.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            })
    @GetMapping
    public ResponseEntity<List<PricingPlanDto>> getAllPricingPlans() {
        return ResponseEntity.ok(pricingPlanService.getAllPricingPlan());
    }

    @Operation(
            summary = "Update pricing plan",
            description = "Updates the details of an existing pricing plan.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully updated the pricing plan",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PricingPlanDto.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "Pricing plan not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            })
    @PutMapping("/{id}")
    public ResponseEntity<PricingPlanDto> updatePricingPlan(
            @Parameter(description = "Pricing Plan ID") @PathVariable UUID id,
            @Valid @RequestBody PricingPlanDto pricingPlanDto) {
        return ResponseEntity.ok(pricingPlanService.updatePricingPlan(id, pricingPlanDto));
    }

    @Operation(
            summary = "Delete pricing plan",
            description = "Deletes a pricing plan by its ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully deleted the pricing plan"),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "Pricing plan not found")
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePricingPlan(
            @Parameter(description = "Pricing Plan ID") @PathVariable UUID id) {
        pricingPlanService.deletePricingPlan(id);
        return ResponseEntity.noContent().build();
    }
}

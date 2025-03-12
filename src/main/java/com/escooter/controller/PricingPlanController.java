package com.escooter.controller;

import com.escooter.dto.PricingPlanDto;
import com.escooter.service.PricingPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pricing-plans")
@RequiredArgsConstructor
public class PricingPlanController {

    private final PricingPlanService pricingPlanService;

    @PostMapping
    public ResponseEntity<PricingPlanDto> addPricingPlan(@RequestBody PricingPlanDto pricingPlanDto) {
        return ResponseEntity.ok(pricingPlanService.addPricingPlan(pricingPlanDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PricingPlanDto> getPricingPlan(@PathVariable UUID id) {
        return ResponseEntity.ok(pricingPlanService.getPricingPlan(id));
    }

    @GetMapping
    public ResponseEntity<List<PricingPlanDto>> getAllPricingPlans() {
        return ResponseEntity.ok(pricingPlanService.getAllPricingPlan());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PricingPlanDto> updatePricingPlan(@PathVariable UUID id, @RequestBody PricingPlanDto pricingPlanDto) {
        return ResponseEntity.ok(pricingPlanService.updatePricingPlan(id, pricingPlanDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePricingPlan(@PathVariable UUID id) {
        pricingPlanService.deletePricingPlan(id);
        return ResponseEntity.noContent().build();
    }
}

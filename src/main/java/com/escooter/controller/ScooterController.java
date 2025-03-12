package com.escooter.controller;

import com.escooter.dto.PricingPlanDto;
import com.escooter.dto.ScooterDto;
import com.escooter.service.ScooterService;
import jakarta.validation.Valid;
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
public class ScooterController {

    private final ScooterService scooterService;

    @PostMapping
    public ResponseEntity<ScooterDto> addScooter(@Valid @RequestBody ScooterDto scooterDto) {
        return ResponseEntity.ok(scooterService.addScooter(scooterDto));
    }

    @GetMapping("/{scooterId}")
    public ResponseEntity<ScooterDto> getScooterById(@PathVariable UUID scooterId) {
        return ResponseEntity.ok(scooterService.getScooterById(scooterId));
    }

    @GetMapping
    public ResponseEntity<List<ScooterDto>> getAllScooters() {
        return ResponseEntity.ok(scooterService.getAllScooters());
    }

    @PutMapping("/{scooterId}")
    public ResponseEntity<ScooterDto> updateScooter(@PathVariable UUID scooterId, @Valid @RequestBody ScooterDto scooterDto) {
        return ResponseEntity.ok(scooterService.updateScooter(scooterId, scooterDto));
    }

    @DeleteMapping("/{scooterId}")
    public ResponseEntity<Void> deleteScooter(@PathVariable UUID scooterId) {
        scooterService.deleteScooter(scooterId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{scooterId}/pricing-plan")
    public ResponseEntity<PricingPlanDto> getPricingPlanByScooterId(@PathVariable UUID scooterId) {
        return ResponseEntity.ok(scooterService.getPricingPlanByScooterId(scooterId));
    }
}

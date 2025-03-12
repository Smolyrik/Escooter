package com.escooter.controller;

import com.escooter.dto.RentalPointDto;
import com.escooter.dto.ScooterDto;
import com.escooter.service.RentalPointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/rental-points")
@RequiredArgsConstructor
public class RentalPointController {

    private final RentalPointService rentalPointService;

    @PostMapping
    public ResponseEntity<RentalPointDto> addRentalPoint(@RequestBody RentalPointDto rentalPointDto) {
        return ResponseEntity.ok(rentalPointService.addRentalPoint(rentalPointDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RentalPointDto> getRentalPointById(@PathVariable UUID id) {
        return ResponseEntity.ok(rentalPointService.getRentalPointById(id));
    }

    @GetMapping
    public ResponseEntity<List<RentalPointDto>> getAllRentalPoints() {
        return ResponseEntity.ok(rentalPointService.getAllRentalPoints());
    }

    @PutMapping("/{id}")
    public ResponseEntity<RentalPointDto> updateRentalPoint(@PathVariable UUID id, @RequestBody RentalPointDto rentalPointDto) {
        return ResponseEntity.ok(rentalPointService.updateRentalPoint(id, rentalPointDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRentalPoint(@PathVariable UUID id) {
        rentalPointService.deleteRentalPoint(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{rentalPointId}/scooters")
    public ResponseEntity<List<ScooterDto>> getAllScootersByRentalPoint(@PathVariable UUID rentalPointId) {
        return ResponseEntity.ok(rentalPointService.getAllScootersByRentalPoint(rentalPointId));
    }

    @GetMapping("/{rentalPointId}/scooters/available")
    public ResponseEntity<List<ScooterDto>> getAvailableScootersByRentalPoint(@PathVariable UUID rentalPointId) {
        return ResponseEntity.ok(rentalPointService.getAvailableScootersByRentalPoint(rentalPointId));
    }

    @GetMapping("/{rentalPointId}/scooters/rented")
    public ResponseEntity<List<ScooterDto>> getRentedScootersByRentalPoint(@PathVariable UUID rentalPointId) {
        return ResponseEntity.ok(rentalPointService.getRentedScootersByRentalPoint(rentalPointId));
    }

    @GetMapping("/{rentalPointId}/scooters/repair")
    public ResponseEntity<List<ScooterDto>> getScootersOnRepairByRentalPoint(@PathVariable UUID rentalPointId) {
        return ResponseEntity.ok(rentalPointService.getScootersOnRepairByRentalPoint(rentalPointId));
    }
}

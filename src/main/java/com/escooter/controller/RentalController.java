package com.escooter.controller;

import com.escooter.dto.RentalDto;
import com.escooter.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;

    @PostMapping("/start")
    public ResponseEntity<RentalDto> rentScooter(@RequestParam UUID userId, @RequestParam UUID scooterId) {
        return ResponseEntity.ok(rentalService.rentScooter(userId, scooterId));
    }

    @GetMapping
    public ResponseEntity<List<RentalDto>> getAllRentals() {
        return ResponseEntity.ok(rentalService.getAllRentals());
    }

    @PostMapping("/end")
    public ResponseEntity<RentalDto> endRental(@RequestParam UUID rentalId) {
        return ResponseEntity.ok(rentalService.endRental(rentalId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RentalDto>> getRentalsByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(rentalService.getRentalsByUserId(userId));
    }

    @GetMapping("/scooter/{scooterId}")
    public ResponseEntity<List<RentalDto>> getRentalsByScooterId(@PathVariable UUID scooterId) {
        return ResponseEntity.ok(rentalService.getRentalsByScooterId(scooterId));
    }
}

package com.escooter.service.impl;

import com.escooter.dto.RentalDto;
import com.escooter.entity.*;
import com.escooter.mapper.RentalMapper;
import com.escooter.repository.*;
import com.escooter.service.RentalService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class RentalServiceImpl implements RentalService {

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final ScooterRepository scooterRepository;
    private final RentalStatusRepository rentalStatusRepository;
    private final RentalMapper rentalMapper;
    private final ScooterStatusRepository scooterStatusRepository;


    @Transactional
    public RentalDto rentScooter(UUID userId, UUID scooterId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with ID: " + userId + " not found"));

        Scooter scooter = scooterRepository.findById(scooterId)
                .orElseThrow(() -> new NoSuchElementException("Scooter with ID: " + scooterId + " not found"));

        boolean isScooterAvailable = scooterRepository.existsByIdAndStatus_Name(scooter.getId(), "Available");
        if (!isScooterAvailable) {
            throw new IllegalStateException("Scooter with ID: " + scooterId + " is already rented");
        }

        ScooterStatus scooterStatus = scooterStatusRepository.findByName("Rented")
                .orElseThrow(() -> new NoSuchElementException("Scooter status 'Rented' not found"));

        RentalStatus activeStatus = rentalStatusRepository.findByName("Active")
                .orElseThrow(() -> new NoSuchElementException("Rental status 'Active' not found"));

        Rental rental = Rental.builder()
                .user(user)
                .scooter(scooter)
                .startTime(LocalDateTime.now())
                .status(activeStatus)
                .build();

        Rental savedRental = rentalRepository.save(rental);

        scooter.setStatus(scooterStatus);
        scooterRepository.save(scooter);

        log.info("Started rental with ID: {}", savedRental.getId());
        return rentalMapper.toDto(savedRental);
    }


    @Transactional(readOnly = true)
    public List<RentalDto> getAllRentals() {
        return rentalRepository.findAll().stream()
                .map(rentalMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RentalDto endRental(UUID rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new NoSuchElementException("Rental with ID: " + rentalId + " not found"));

        RentalStatus activeStatus = rentalStatusRepository.findByName("Completed")
                .orElseThrow(() -> new NoSuchElementException("Rental status 'Completed' not found"));

        ScooterStatus availableStatus = scooterStatusRepository.findByName("Available")
                .orElseThrow(() -> new NoSuchElementException("Scooter status 'Available' not found"));

        rental.setEndTime(LocalDateTime.now());
        rental.setStatus(activeStatus);
        rental.setTotalPrice(calculateTotalPrice(rental));

        Scooter scooter = rental.getScooter();
        scooter.setStatus(availableStatus);
        scooterRepository.save(scooter);

        Rental savedRental = rentalRepository.save(rental);
        log.info("Ended rental with ID: {}", savedRental.getId());
        return rentalMapper.toDto(savedRental);
    }

    @Transactional(readOnly = true)
    public List<RentalDto> getRentalsByUserId(UUID userId) {
        return rentalRepository.findByUserId(userId).stream()
                .map(rentalMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RentalDto> getRentalsByScooterId(UUID scooterId) {
        return rentalRepository.findByScooterId(scooterId).stream()
                .map(rentalMapper::toDto)
                .collect(Collectors.toList());
    }


    //Надо переработать
    private BigDecimal calculateTotalPrice(Rental rental) {
        Duration duration = Duration.between(rental.getStartTime(), rental.getEndTime());
        BigDecimal hours = BigDecimal.valueOf(duration.toMinutes()).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
        BigDecimal pricePerHour = rental.getScooter().getPricingPlan().getPricePerHour();
        BigDecimal discount = rental.getScooter().getPricingPlan().getDiscount();

        BigDecimal totalPrice = pricePerHour.multiply(hours);
        if (discount != null && discount.compareTo(BigDecimal.ZERO) > 0) {
            totalPrice = totalPrice.subtract(totalPrice.multiply(discount.divide(BigDecimal.valueOf(100))));
        }
        return totalPrice;
    }
}

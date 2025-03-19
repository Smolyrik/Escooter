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
    private final RentalTypeRepository rentalTypeRepository;
    private final RentalMapper rentalMapper;
    private final ScooterStatusRepository scooterStatusRepository;


    @Transactional
    public RentalDto rentScooter(UUID userId, UUID scooterId, Integer rentalTypeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with ID: " + userId + " not found"));

        if (user.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("User with ID: " + userId + " has a negative balance and cannot rent a scooter.");
        }

        Scooter scooter = scooterRepository.findById(scooterId)
                .orElseThrow(() -> new NoSuchElementException("Scooter with ID: " + scooterId + " not found"));

        RentalType rentalType = rentalTypeRepository.findById(rentalTypeId)
                .orElseThrow(() -> new NoSuchElementException("Rental type with ID: " + rentalTypeId + " not found"));

        boolean isScooterAvailable = scooterRepository.existsByIdAndStatus_Name(scooter.getId(), "AVAILABLE");
        if (!isScooterAvailable) {
            throw new IllegalStateException("Scooter with ID: " + scooterId + " is already rented");
        }

        ScooterStatus scooterStatus = scooterStatusRepository.findByName("RENTED")
                .orElseThrow(() -> new NoSuchElementException("Scooter status 'Rented' not found"));

        RentalStatus activeStatus = rentalStatusRepository.findByName("ACTIVE")
                .orElseThrow(() -> new NoSuchElementException("Rental status 'Active' not found"));

        Rental rental = Rental.builder()
                .user(user)
                .scooter(scooter)
                .startTime(LocalDateTime.now())
                .status(activeStatus)
                .rentalType(rentalType)
                .distance(BigDecimal.ZERO)
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
    public RentalDto endRental(UUID rentalId, BigDecimal distance) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new NoSuchElementException("Rental with ID: " + rentalId + " not found"));

        RentalStatus completedStatus = rentalStatusRepository.findByName("COMPLETED")
                .orElseThrow(() -> new NoSuchElementException("Rental status 'Completed' not found"));

        ScooterStatus availableStatus = scooterStatusRepository.findByName("AVAILABLE")
                .orElseThrow(() -> new NoSuchElementException("Scooter status 'Available' not found"));

        rental.setEndTime(LocalDateTime.now());
        rental.setStatus(completedStatus);
        BigDecimal totalPrice = calculateTotalPrice(rental);
        rental.setTotalPrice(totalPrice);
        rental.setDistance(distance);

        User user = rental.getUser();
        BigDecimal newBalance = user.getBalance().subtract(totalPrice);
        user.setBalance(newBalance);
        userRepository.save(user);

        Scooter scooter = rental.getScooter();
        scooter.setStatus(availableStatus);
        scooter.setMileage(scooter.getMileage().add(distance));
        scooterRepository.save(scooter);

        Rental savedRental = rentalRepository.save(rental);
        log.info("Ended rental with ID: {}, User balance after payment: {}", savedRental.getId(), user.getBalance());

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

    private BigDecimal calculateTotalPrice(Rental rental) {
        Duration duration = Duration.between(rental.getStartTime(), rental.getEndTime());
        BigDecimal hours = BigDecimal.valueOf(duration.toMinutes()).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

        RentalType rentalType = rental.getRentalType();
        BigDecimal pricePerHour = rental.getScooter().getPricingPlan().getPricePerHour();
        BigDecimal subscriptionPrice = rental.getScooter().getPricingPlan().getSubscriptionPrice();
        BigDecimal discount = rental.getScooter().getPricingPlan().getDiscount();
        BigDecimal totalPrice;

        if ("Hourly".equalsIgnoreCase(rentalType.getName())) {
            totalPrice = pricePerHour.multiply(hours);
        } else if ("Subscription".equalsIgnoreCase(rentalType.getName())) {
            totalPrice = subscriptionPrice.multiply(hours);
        } else {
            throw new IllegalStateException("Unknown rental type: " + rentalType.getName());
        }

        if (discount != null && discount.compareTo(BigDecimal.ZERO) > 0) {
            totalPrice = totalPrice.subtract(totalPrice.multiply(discount.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)));
        }

        return totalPrice;
    }
}

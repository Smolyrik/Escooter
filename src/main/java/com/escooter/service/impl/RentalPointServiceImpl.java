package com.escooter.service.impl;

import com.escooter.dto.RentalPointDto;
import com.escooter.dto.ScooterDto;
import com.escooter.entity.RentalPoint;
import com.escooter.mapper.RentalPointMapper;
import com.escooter.mapper.ScooterMapper;
import com.escooter.repository.RentalPointRepository;
import com.escooter.repository.ScooterRepository;
import com.escooter.service.RentalPointService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class RentalPointServiceImpl implements RentalPointService {

    private final ScooterRepository scooterRepository;
    private final ScooterMapper scooterMapper;
    private final RentalPointRepository rentalPointRepository;
    private final RentalPointMapper rentalPointMapper;

    @Transactional
    public RentalPointDto addRentalPoint(RentalPointDto rentalPointDto) {
        RentalPoint rentalPoint = rentalPointMapper.toEntity(rentalPointDto);
        RentalPoint savedPoint = rentalPointRepository.save(rentalPoint);
        log.info("Added new rental point with ID: {}", savedPoint.getId());
        return rentalPointMapper.toDto(savedPoint);
    }

    @Transactional(readOnly = true)
    public RentalPointDto getRentalPointById(UUID id) {
        return rentalPointRepository.findById(id)
                .map(rentalPointMapper::toDto)
                .orElseThrow(() -> {
                    log.error("Rental point with ID: {} not found", id);
                    return new NoSuchElementException("Rental point with ID: " + id + " not found");
                });
    }

    @Transactional(readOnly = true)
    public List<RentalPointDto> getAllRentalPoints() {
        log.info("Fetching all rental points");
        return rentalPointRepository.findAll().stream()
                .map(rentalPointMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RentalPointDto updateRentalPoint(UUID id, RentalPointDto rentalPointDto) {
        if (!rentalPointRepository.existsById(id)) {
            log.error("Rental point with ID: {} not found", id);
            throw new NoSuchElementException("Rental point with ID: " + id + " not found");
        }

        RentalPoint updatedPoint = rentalPointMapper.toEntity(rentalPointDto);
        updatedPoint.setId(id);

        RentalPoint savedPoint = rentalPointRepository.save(updatedPoint);
        log.info("Updated rental point with ID: {}", savedPoint.getId());

        return rentalPointMapper.toDto(savedPoint);
    }

    @Transactional
    public void deleteRentalPoint(UUID id) {
        if (!rentalPointRepository.existsById(id)) {
            log.error("Rental point with ID: {} not found", id);
            throw new NoSuchElementException("Rental point with ID: " + id + " not found");
        }
        rentalPointRepository.deleteById(id);
        log.info("Deleted rental point with ID: {}", id);
    }

    @Transactional(readOnly = true)
    public List<ScooterDto> getAllScootersByRentalPoint(UUID rentalPointId) {
        log.info("Fetching all scooters for rental point ID: {}", rentalPointId);
        return scooterRepository.findByRentalPointId(rentalPointId).stream()
                .map(scooterMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ScooterDto> getAvailableScootersByRentalPoint(UUID rentalPointId) {
        log.info("Fetching available scooters for rental point ID: {}", rentalPointId);
        return scooterRepository.getAvailableScootersByRentalPointId(rentalPointId).stream()
                .map(scooterMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ScooterDto> getRentedScootersByRentalPoint(UUID rentalPointId) {
        log.info("Fetching rented scooters for rental point ID: {}", rentalPointId);
        return scooterRepository.getRentedScootersByRentalPointId(rentalPointId).stream()
                .map(scooterMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ScooterDto> getScootersOnRepairByRentalPoint(UUID rentalPointId) {
        log.info("Fetching scooters on repair for rental point ID: {}", rentalPointId);
        return scooterRepository.getInRepairScootersByRentalPointId(rentalPointId).stream()
                .map(scooterMapper::toDto)
                .collect(Collectors.toList());
    }
}

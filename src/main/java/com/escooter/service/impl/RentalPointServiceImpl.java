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

import java.math.BigDecimal;
import java.util.Comparator;
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
        log.info("Adding new rental point: {}", rentalPointDto);
        RentalPoint rentalPoint = rentalPointMapper.toEntity(rentalPointDto);
        RentalPoint savedPoint = rentalPointRepository.save(rentalPoint);
        log.info("Successfully added rental point with ID: {}", savedPoint.getId());
        return rentalPointMapper.toDto(savedPoint);
    }

    @Transactional(readOnly = true)
    public RentalPointDto getRentalPointById(UUID id) {
        log.info("Fetching rental point with ID: {}", id);
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
        List<RentalPointDto> points = rentalPointRepository.findAll().stream()
                .map(rentalPointMapper::toDto)
                .collect(Collectors.toList());
        log.info("Total rental points found: {}", points.size());
        return points;
    }

    @Transactional(readOnly = true)
    public List<RentalPointDto> getNearbyRentalPoints(BigDecimal userLat, BigDecimal userLon) {
        log.info("Fetching nearby rental points for location: {}, {}", userLat, userLon);

        double userLatDouble = userLat.doubleValue();
        double userLonDouble = userLon.doubleValue();

        List<RentalPointDto> points = rentalPointRepository.findAll().stream()
                .sorted(Comparator.comparingDouble(point -> haversineDistance(
                        userLatDouble, userLonDouble,
                        point.getLatitude().doubleValue(), point.getLongitude().doubleValue())))
                .map(rentalPointMapper::toDto)
                .collect(Collectors.toList());
        log.info("Total nearby rental points found: {}", points.size());
        return points;
    }

    @Transactional
    public RentalPointDto updateRentalPoint(UUID id, RentalPointDto rentalPointDto) {
        log.info("Updating rental point with ID: {}", id);
        if (!rentalPointRepository.existsById(id)) {
            log.error("Rental point with ID: {} not found", id);
            throw new NoSuchElementException("Rental point with ID: " + id + " not found");
        }
        RentalPoint updatedPoint = rentalPointMapper.toEntity(rentalPointDto);
        updatedPoint.setId(id);
        RentalPoint savedPoint = rentalPointRepository.save(updatedPoint);
        log.info("Successfully updated rental point with ID: {}", savedPoint.getId());
        return rentalPointMapper.toDto(savedPoint);
    }

    @Transactional
    public void deleteRentalPoint(UUID id) {
        log.info("Deleting rental point with ID: {}", id);
        if (!rentalPointRepository.existsById(id)) {
            log.error("Rental point with ID: {} not found", id);
            throw new NoSuchElementException("Rental point with ID: " + id + " not found");
        }
        rentalPointRepository.deleteById(id);
        log.info("Successfully deleted rental point with ID: {}", id);
    }

    @Transactional(readOnly = true)
    public List<ScooterDto> getAllScootersByRentalPoint(UUID rentalPointId) {
        log.info("Fetching all scooters for rental point ID: {}", rentalPointId);
        List<ScooterDto> scooters = scooterRepository.findByRentalPointId(rentalPointId).stream()
                .map(scooterMapper::toDto)
                .collect(Collectors.toList());
        log.info("Total scooters found: {}", scooters.size());
        return scooters;
    }

    @Transactional(readOnly = true)
    public List<ScooterDto> getAvailableScootersByRentalPoint(UUID rentalPointId) {
        log.info("Fetching available scooters for rental point ID: {}", rentalPointId);
        List<ScooterDto> scooters = scooterRepository.getAvailableScootersByRentalPointId(rentalPointId).stream()
                .map(scooterMapper::toDto)
                .collect(Collectors.toList());
        log.info("Total available scooters found: {}", scooters.size());
        return scooters;
    }

    @Transactional(readOnly = true)
    public List<ScooterDto> getRentedScootersByRentalPoint(UUID rentalPointId) {
        log.info("Fetching rented scooters for rental point ID: {}", rentalPointId);
        List<ScooterDto> scooters = scooterRepository.getRentedScootersByRentalPointId(rentalPointId).stream()
                .map(scooterMapper::toDto)
                .collect(Collectors.toList());
        log.info("Total rented scooters found: {}", scooters.size());
        return scooters;
    }

    @Transactional(readOnly = true)
    public List<ScooterDto> getScootersOnRepairByRentalPoint(UUID rentalPointId) {
        log.info("Fetching scooters on repair for rental point ID: {}", rentalPointId);
        List<ScooterDto> scooters = scooterRepository.getInRepairScootersByRentalPointId(rentalPointId).stream()
                .map(scooterMapper::toDto)
                .collect(Collectors.toList());
        log.info("Total on repair scooters found: {}", scooters.size());
        return scooters;
    }

    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;
        log.debug("Calculated Haversine distance: {} km", distance);
        return distance;
    }
}

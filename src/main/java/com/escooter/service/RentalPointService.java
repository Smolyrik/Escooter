package com.escooter.service;

import com.escooter.dto.RentalPointDto;
import com.escooter.dto.ScooterDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface RentalPointService {

    RentalPointDto addRentalPoint(RentalPointDto rentalPointDto);

    RentalPointDto getRentalPointById(UUID id);

    List<RentalPointDto> getAllRentalPoints();

    List<RentalPointDto> getNearbyRentalPoints(BigDecimal userLat, BigDecimal userLon);

    RentalPointDto updateRentalPoint(UUID id, RentalPointDto rentalPointDto);

    void deleteRentalPoint(UUID id);

    List<ScooterDto> getAllScootersByRentalPoint(UUID rentalPointId);

    List<ScooterDto> getAvailableScootersByRentalPoint(UUID rentalPointId);

    List<ScooterDto> getRentedScootersByRentalPoint(UUID rentalPointId);

    List<ScooterDto> getScootersOnRepairByRentalPoint(UUID rentalPointId);
}

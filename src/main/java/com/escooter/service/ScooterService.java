package com.escooter.service;

import com.escooter.dto.PricingPlanDto;
import com.escooter.dto.ScooterDto;

import java.util.List;
import java.util.UUID;

public interface ScooterService {

    ScooterDto addScooter(ScooterDto scooterDto);

    ScooterDto getScooterById(UUID scooterId);

    List<ScooterDto> getAllScooters();

    ScooterDto updateScooter(UUID scooterId, ScooterDto scooterDto);

    void deleteScooter(UUID scooterId);

    PricingPlanDto getPricingPlanByScooterId(UUID scooterId);
}

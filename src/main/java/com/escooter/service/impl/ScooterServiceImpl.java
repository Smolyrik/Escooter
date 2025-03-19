package com.escooter.service.impl;

import com.escooter.dto.PricingPlanDto;
import com.escooter.dto.ScooterDto;
import com.escooter.entity.Scooter;
import com.escooter.mapper.PricingPlanMapper;
import com.escooter.mapper.ScooterMapper;
import com.escooter.repository.ScooterRepository;
import com.escooter.service.ScooterService;
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
public class ScooterServiceImpl implements ScooterService {

    private final ScooterRepository scooterRepository;
    private final ScooterMapper scooterMapper;
    private final PricingPlanMapper pricingPlanMapper;

    @Transactional
    public ScooterDto addScooter(ScooterDto scooterDto) {
        log.info("Adding a new scooter");
        Scooter scooter = scooterMapper.toEntity(scooterDto);
        Scooter savedScooter = scooterRepository.save(scooter);
        log.info("Added new scooter with ID: {}", savedScooter.getId());
        return scooterMapper.toDto(savedScooter);
    }

    @Transactional(readOnly = true)
    public ScooterDto getScooterById(UUID scooterId) {
        log.info("Fetching scooter with ID: {}", scooterId);
        return scooterRepository.findById(scooterId)
                .map(scooterMapper::toDto)
                .orElseThrow(() -> {
                    log.error("Scooter with ID: {} not found", scooterId);
                    return new NoSuchElementException("Scooter with ID: " + scooterId + " not found");
                });
    }

    @Transactional(readOnly = true)
    public List<ScooterDto> getAllScooters() {
        log.info("Fetching all scooters");
        return scooterRepository.findAll().stream()
                .map(scooterMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ScooterDto updateScooter(UUID scooterId, ScooterDto scooterDto) {
        log.info("Updating scooter with ID: {}", scooterId);
        if (!scooterRepository.existsById(scooterId)) {
            log.error("Scooter with ID: {} not found", scooterId);
            throw new NoSuchElementException("Scooter with ID: " + scooterId + " not found");
        }

        Scooter updatedScooter = scooterMapper.toEntity(scooterDto);
        updatedScooter.setId(scooterId);

        Scooter savedScooter = scooterRepository.save(updatedScooter);
        log.info("Successfully updated scooter with ID: {}", savedScooter.getId());

        return scooterMapper.toDto(savedScooter);
    }

    @Transactional
    public void deleteScooter(UUID scooterId) {
        log.info("Attempting to delete scooter with ID: {}", scooterId);
        if (!scooterRepository.existsById(scooterId)) {
            log.error("Scooter with ID: {} not found", scooterId);
            throw new NoSuchElementException("Scooter with ID: " + scooterId + " not found");
        }
        scooterRepository.deleteById(scooterId);
        log.info("Successfully deleted scooter with ID: {}", scooterId);
    }

    @Transactional(readOnly = true)
    public PricingPlanDto getPricingPlanByScooterId(UUID scooterId) {
        log.info("Fetching pricing plan for scooter ID: {}", scooterId);
        Scooter scooter = scooterRepository.findById(scooterId)
                .orElseThrow(() -> {
                    log.error("Scooter with ID: {} not found", scooterId);
                    return new NoSuchElementException("Scooter with ID: " + scooterId + " not found");
                });

        if (scooter.getPricingPlan() == null) {
            log.warn("Scooter with ID: {} has no pricing plan assigned", scooterId);
            throw new NoSuchElementException("Scooter with ID: " + scooterId + " has no pricing plan assigned");
        }

        log.info("Successfully retrieved pricing plan for scooter ID: {}", scooterId);
        return pricingPlanMapper.toDto(scooter.getPricingPlan());
    }
}

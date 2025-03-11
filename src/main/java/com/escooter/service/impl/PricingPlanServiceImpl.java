package com.escooter.service.impl;

import com.escooter.dto.PricingPlanDto;
import com.escooter.entity.PricingPlan;
import com.escooter.mapper.PricingPlanMapper;
import com.escooter.repository.PricingPlanRepository;
import com.escooter.service.PricingPlanService;
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
public class PricingPlanServiceImpl implements PricingPlanService {

    private final PricingPlanRepository pricingPlanRepository;
    private final PricingPlanMapper pricingPlanMapper;

    @Transactional
    public PricingPlanDto addPricingPlan(PricingPlanDto pricingPlanDto) {
        PricingPlan pricingPlan = pricingPlanMapper.toEntity(pricingPlanDto);
        PricingPlan savedPlan = pricingPlanRepository.save(pricingPlan);
        log.info("Added new pricing plan with ID: {}", savedPlan.getId());
        return pricingPlanMapper.toDto(savedPlan);
    }

    @Transactional(readOnly = true)
    public PricingPlanDto getPricingPlan(UUID id) {
        return pricingPlanRepository.findById(id)
                .map(pricingPlanMapper::toDto)
                .orElseThrow(() -> {
                    log.error("Pricing plan with ID: {} not found", id);
                    return new NoSuchElementException("Pricing plan with ID: " + id + " not found");
                });
    }

    @Transactional(readOnly = true)
    public List<PricingPlanDto> getAllPricingPlan() {
        log.info("Fetching all pricing plans");
        return pricingPlanRepository.findAll().stream()
                .map(pricingPlanMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public PricingPlanDto updatePricingPlan(UUID id, PricingPlanDto pricingPlanDto) {
        if (!pricingPlanRepository.existsById(id)) {
            log.error("Pricing plan with ID: {} not found", id);
            throw new NoSuchElementException("Pricing plan with ID: " + id + " not found");
        }

        PricingPlan updatedPlan = pricingPlanMapper.toEntity(pricingPlanDto);
        updatedPlan.setId(id);

        PricingPlan savedPlan = pricingPlanRepository.save(updatedPlan);
        log.info("Updated pricing plan with ID: {}", savedPlan.getId());

        return pricingPlanMapper.toDto(savedPlan);
    }

    @Transactional
    public void deletePricingPlan(UUID id) {
        if (!pricingPlanRepository.existsById(id)) {
            log.error("Pricing plan with ID: {} not found", id);
            throw new NoSuchElementException("Pricing plan with ID: " + id + " not found");
        }
        pricingPlanRepository.deleteById(id);
        log.info("Deleted pricing plan with ID: {}", id);
    }
}

package com.escooter.service;

import com.escooter.dto.PricingPlanDto;

import java.util.List;
import java.util.UUID;

public interface PricingPlanService {

    PricingPlanDto addPricingPlan(PricingPlanDto pricingPlan);

    PricingPlanDto getPricingPlan(UUID id);

    List<PricingPlanDto> getAllPricingPlan();

    PricingPlanDto updatePricingPlan(UUID id, PricingPlanDto pricingPlan);

    void deletePricingPlan(UUID id);
}

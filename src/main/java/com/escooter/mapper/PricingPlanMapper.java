package com.escooter.mapper;

import com.escooter.dto.PricingPlanDto;
import com.escooter.entity.PricingPlan;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PricingPlanMapper {

    PricingPlanDto toDto(PricingPlan pricingPlan);

    PricingPlan toEntity(PricingPlanDto pricingPlanDto);

}

package com.escooter.mapper;

import com.escooter.dto.ScooterDto;
import com.escooter.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ScooterMapper {

    @Mapping(source = "rentalPoint.id", target = "rentalPointId")
    @Mapping(source = "model.id", target = "modelId")
    @Mapping(source = "status.id", target = "statusId")
    @Mapping(source = "pricingPlan.id", target = "pricingPlanId")
    ScooterDto toDto(Scooter scooter);

    @Mapping(source = "rentalPointId", target = "rentalPoint", qualifiedByName = "rentalPointFromId")
    @Mapping(source = "modelId", target = "model", qualifiedByName = "modelFromId")
    @Mapping(source = "statusId", target = "status", qualifiedByName = "statusFromId")
    @Mapping(source = "pricingPlanId", target = "pricingPlan", qualifiedByName = "pricingPlanFromId")
    Scooter toEntity(ScooterDto scooterDto);

    @Named("rentalPointFromId")
    default RentalPoint rentalPointFromId(UUID rentalPointId){
        if(rentalPointId == null){
            return null;
        }
        return RentalPoint.builder()
                .id(rentalPointId)
                .build();
    }

    @Named("modelFromId")
    default Model modelFromId(Integer modelId){
        if(modelId == null){
            return null;
        }
        return Model.builder()
                .id(modelId)
                .build();
    }

    @Named("statusFromId")
    default ScooterStatus statusFromId(Integer statusId){
        if(statusId == null){
            return null;
        }
        return ScooterStatus.builder()
                .id(statusId)
                .build();
    }

    @Named("pricingPlanFromId")
    default PricingPlan pricingPlanFromId(UUID pricingPlanId){
        if(pricingPlanId == null){
            return null;
        }
        return PricingPlan.builder()
                .id(pricingPlanId)
                .build();
    }
}

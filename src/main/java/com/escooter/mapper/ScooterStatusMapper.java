package com.escooter.mapper;

import com.escooter.dto.ScooterStatusDto;
import com.escooter.entity.ScooterStatus;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ScooterStatusMapper {

    ScooterStatusDto toDto(ScooterStatus scooterStatus);

    ScooterStatus toEntity(ScooterStatusDto scooterStatus);

}

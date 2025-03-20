package com.escooter.mapper;

import com.escooter.dto.RentalTypeDto;
import com.escooter.entity.RentalType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RentalTypeMapper {

    RentalTypeDto toDto(RentalType rentalType);

    RentalType toEntity(RentalTypeDto rentalTypeDto);
}

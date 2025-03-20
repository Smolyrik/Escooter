package com.escooter.mapper;

import com.escooter.dto.RentalStatusDto;
import com.escooter.entity.RentalStatus;

public interface RentalStatusMapper {

    RentalStatusDto toDto(RentalStatus rentalStatus);

    RentalStatus toEntity(RentalStatusDto rentalStatusDto);
}

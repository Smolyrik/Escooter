package com.escooter.mapper;

import com.escooter.dto.PaymentStatusDto;
import com.escooter.entity.PaymentStatus;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentStatusMapper {

    PaymentStatusDto toDto(PaymentStatus paymentStatus);

    PaymentStatus toEntity(PaymentStatusDto paymentStatusDto);
}

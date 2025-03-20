package com.escooter.mapper;

import com.escooter.dto.PaymentDto;
import com.escooter.entity.Payment;
import com.escooter.entity.PaymentStatus;
import com.escooter.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "status.id", target = "statusId")
    PaymentDto toDto(Payment payment);

    @Mapping(source = "userId", target = "user", qualifiedByName = "userFromId")
    @Mapping(source = "statusId", target = "status", qualifiedByName = "statusFromId")
    Payment toEntity(PaymentDto paymentDto);

    @Named("userFromId")
    default User userFromId(UUID userId){
        if(userId == null){
            return null;
        }
        return User.builder()
                .id(userId)
                .build();
    }

    @Named("statusFromId")
    default PaymentStatus statusFromId(Integer statusId){
        if(statusId == null){
            return null;
        }
        return PaymentStatus.builder()
                .id(statusId)
                .build();
    }
}

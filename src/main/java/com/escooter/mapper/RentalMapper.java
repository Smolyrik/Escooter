package com.escooter.mapper;

import com.escooter.dto.RentalDto;
import com.escooter.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface RentalMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "scooter.id", target = "scooterId")
    @Mapping(source = "status.id", target = "statusId")
    @Mapping(source = "rentalType.id", target = "rentalTypeId")
    RentalDto toDto(Rental rental);

    @Mapping(source = "userId", target = "user", qualifiedByName = "userFromId")
    @Mapping(source = "scooterId", target = "scooter", qualifiedByName = "scooterFromId")
    @Mapping(source = "statusId", target = "status", qualifiedByName = "rentalStatusFromId")
    @Mapping(source = "rentalTypeId", target = "rentalType", qualifiedByName = "rentalTypeFromId")
    Rental toEntity(RentalDto rentalDto);


    @Named("userFromId")
    default User userFromId(UUID userId){
        if(userId == null){
            return null;
        }
        return User.builder()
                .id(userId)
                .build();
    }

    @Named("scooterFromId")
    default Scooter scooterFromId(UUID scooterId){
        if(scooterId == null){
            return null;
        }
        return Scooter.builder()
                .id(scooterId)
                .build();
    }

    @Named("rentalStatusFromId")
    default RentalStatus rentalStatusFromId(Integer rentalId){
        if(rentalId == null){
            return null;
        }
        return RentalStatus.builder()
                .id(rentalId)
                .build();
    }

    @Named("rentalTypeFromId")
    default RentalType rentalTypeFromId(Integer rentalTypeId){
        if(rentalTypeId == null){
            return null;
        }
        return RentalType.builder()
                .id(rentalTypeId)
                .build();
    }
}

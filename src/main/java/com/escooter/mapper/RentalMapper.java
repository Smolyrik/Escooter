package com.escooter.mapper;

import com.escooter.dto.RentalDto;
import com.escooter.entity.Rental;
import com.escooter.entity.RentalStatus;
import com.escooter.entity.Scooter;
import com.escooter.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface RentalMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "scooter.id", target = "scooterId")
    @Mapping(source = "status.id", target = "statusId")
    RentalDto toDto(Rental rental);

    @Mapping(source = "userId", target = "user", qualifiedByName = "userFromId")
    @Mapping(source = "scooterId", target = "scooter", qualifiedByName = "scooterFromId")
    @Mapping(source = "statusId", target = "status", qualifiedByName = "rentalStatusFromId")
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
}

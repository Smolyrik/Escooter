package com.escooter.mapper;

import com.escooter.dto.RentalPointDto;
import com.escooter.entity.RentalPoint;
import com.escooter.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface RentalPointMapper {

    @Mapping(source = "manager.id", target = "managerId")
    RentalPointDto toDto(RentalPoint rentalPoint);

    @Mapping(source = "managerId", target = "manager", qualifiedByName = "userFromId")
    RentalPoint toEntity(RentalPointDto rentalPointDto);

    @Named("userFromId")
    default User userFromId(UUID managerId) {
        if (managerId == null) {
            return null;
        }
        return User.builder()
                .id(managerId)
                .build();
    }


}

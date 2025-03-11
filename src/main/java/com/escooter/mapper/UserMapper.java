package com.escooter.mapper;

import com.escooter.dto.UserDto;
import com.escooter.entity.Role;
import com.escooter.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "role.id", target = "roleId")
    UserDto toDto(User user);

    @Mapping(source = "roleId", target = "role", qualifiedByName = "roleFromId")
    User toEntity(UserDto userDto);

    @Named("roleFromId")
    default Role roleFromId(Integer roleId) {
        if (roleId == null) {
            return null;
        }
        return Role.builder()
                .id(roleId)
                .build();
    }
}

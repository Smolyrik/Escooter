package com.escooter.mapper;

import com.escooter.dto.RoleDto;
import com.escooter.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleDto toDto(Role role);

    Role toEntity(RoleDto roleDto);

}

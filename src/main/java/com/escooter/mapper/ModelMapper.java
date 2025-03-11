package com.escooter.mapper;

import com.escooter.dto.ModelDto;
import com.escooter.entity.Model;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ModelMapper {

    ModelDto toDto(Model model);

    Model toEntity(ModelDto modelDto);
}

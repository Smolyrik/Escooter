package com.escooter.mapper;

import com.escooter.dto.ReportDto;
import com.escooter.entity.Report;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReportMapper {

    ReportDto toDto(Report report);

    Report toEntity(ReportDto reportDto);

}

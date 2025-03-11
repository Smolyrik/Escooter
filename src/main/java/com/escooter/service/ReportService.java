package com.escooter.service;

import com.escooter.dto.ReportDto;

import java.util.List;
import java.util.UUID;

public interface ReportService {

    ReportDto addReport(ReportDto report);

    ReportDto getReport(UUID id);

    List<ReportDto> getReports();

    ReportDto updateReport(UUID id, ReportDto report);

    void deleteReport(UUID id);
}

package com.escooter.service.impl;

import com.escooter.dto.ReportDto;
import com.escooter.entity.Report;
import com.escooter.mapper.ReportMapper;
import com.escooter.repository.ReportRepository;
import com.escooter.service.ReportService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;

    @Transactional
    public ReportDto addReport(ReportDto reportDto) {
        Report report = reportMapper.toEntity(reportDto);
        Report savedReport = reportRepository.save(report);
        log.info("Added new report with ID: {}", savedReport.getId());
        return reportMapper.toDto(savedReport);
    }

    @Transactional(readOnly = true)
    public ReportDto getReport(UUID id) {
        return reportRepository.findById(id)
                .map(reportMapper::toDto)
                .orElseThrow(() -> {
                    log.error("Report with ID: {} not found", id);
                    return new NoSuchElementException("Report with ID: " + id + " not found");
                });
    }

    @Transactional(readOnly = true)
    public List<ReportDto> getReports() {
        log.info("Fetching all reports");
        return reportRepository.findAll().stream()
                .map(reportMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReportDto updateReport(UUID id, ReportDto reportDto) {
        if (!reportRepository.existsById(id)) {
            log.error("Report with ID: {} not found", id);
            throw new NoSuchElementException("Report with ID: " + id + " not found");
        }

        Report updatedReport = reportMapper.toEntity(reportDto);
        updatedReport.setId(id);

        Report savedReport = reportRepository.save(updatedReport);
        log.info("Updated report with ID: {}", savedReport.getId());

        return reportMapper.toDto(savedReport);
    }

    @Transactional
    public void deleteReport(UUID id) {
        if (!reportRepository.existsById(id)) {
            log.error("Report with ID: {} not found", id);
            throw new NoSuchElementException("Report with ID: " + id + " not found");
        }
        reportRepository.deleteById(id);
        log.info("Deleted report with ID: {}", id);
    }
}

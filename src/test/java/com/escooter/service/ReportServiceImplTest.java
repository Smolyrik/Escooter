package com.escooter.service;

import com.escooter.dto.ReportDto;
import com.escooter.entity.Report;
import com.escooter.mapper.ReportMapper;
import com.escooter.repository.ReportRepository;
import com.escooter.service.impl.ReportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private ReportMapper reportMapper;

    @InjectMocks
    private ReportServiceImpl reportService;

    private Report report;
    private ReportDto reportDto;
    private UUID reportId;

    @BeforeEach
    void setUp() {
        reportId = UUID.randomUUID();
        report = Report.builder()
                .id(reportId)
                .reportType("Test Report")
                .createdAt(LocalDateTime.now())
                .data("{\"key\":\"value\"}")
                .build();

        reportDto = ReportDto.builder()
                .id(reportId)
                .reportType("Test Report")
                .createdAt(LocalDateTime.now())
                .data("{\"key\":\"value\"}")
                .build();
    }

    @Test
    void addReport_ShouldSaveAndReturnReport() {
        when(reportMapper.toEntity(reportDto)).thenReturn(report);
        when(reportRepository.save(report)).thenReturn(report);
        when(reportMapper.toDto(report)).thenReturn(reportDto);

        ReportDto savedReport = reportService.addReport(reportDto);

        assertNotNull(savedReport);
        assertEquals(reportId, savedReport.getId());
        verify(reportRepository, times(1)).save(report);
    }

    @Test
    void getReport_ShouldReturnReport_WhenExists() {
        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));
        when(reportMapper.toDto(report)).thenReturn(reportDto);

        ReportDto foundReport = reportService.getReport(reportId);

        assertNotNull(foundReport);
        assertEquals(reportId, foundReport.getId());
    }

    @Test
    void getReport_ShouldThrowException_WhenNotFound() {
        when(reportRepository.findById(reportId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> reportService.getReport(reportId));
    }

    @Test
    void getReports_ShouldReturnAllReports() {
        when(reportRepository.findAll()).thenReturn(List.of(report));
        when(reportMapper.toDto(report)).thenReturn(reportDto);

        List<ReportDto> reports = reportService.getReports();

        assertFalse(reports.isEmpty());
        assertEquals(1, reports.size());
    }

    @Test
    void updateReport_ShouldUpdateAndReturnReport_WhenExists() {
        when(reportRepository.existsById(reportId)).thenReturn(true);
        when(reportMapper.toEntity(reportDto)).thenReturn(report);
        when(reportRepository.save(report)).thenReturn(report);
        when(reportMapper.toDto(report)).thenReturn(reportDto);

        ReportDto updatedReport = reportService.updateReport(reportId, reportDto);

        assertNotNull(updatedReport);
        assertEquals(reportId, updatedReport.getId());
        verify(reportRepository, times(1)).save(report);
    }

    @Test
    void updateReport_ShouldThrowException_WhenNotFound() {
        when(reportRepository.existsById(reportId)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> reportService.updateReport(reportId, reportDto));
    }

    @Test
    void deleteReport_ShouldDeleteReport_WhenExists() {
        when(reportRepository.existsById(reportId)).thenReturn(true);
        doNothing().when(reportRepository).deleteById(reportId);

        assertDoesNotThrow(() -> reportService.deleteReport(reportId));
        verify(reportRepository, times(1)).deleteById(reportId);
    }

    @Test
    void deleteReport_ShouldThrowException_WhenNotFound() {
        when(reportRepository.existsById(reportId)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> reportService.deleteReport(reportId));
    }
}

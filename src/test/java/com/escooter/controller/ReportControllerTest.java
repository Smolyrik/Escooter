package com.escooter.controller;

import com.escooter.dto.ReportDto;
import com.escooter.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    @Mock
    private ReportService reportService;

    @InjectMocks
    private ReportController reportController;

    private ReportDto reportDto;
    private UUID reportId;

    @BeforeEach
    void setUp() {
        reportId = UUID.randomUUID();
        reportDto = ReportDto.builder()
                .id(reportId)
                .reportType("Usage")
                .createdAt(LocalDateTime.now())
                .data("Report data")
                .build();
    }

    @Test
    void addReport_ShouldReturnReport() {
        when(reportService.addReport(any(ReportDto.class))).thenReturn(reportDto);
        ResponseEntity<ReportDto> response = reportController.addReport(reportDto);

        assertNotNull(response.getBody());
        assertEquals(reportDto.getId(), response.getBody().getId());
        verify(reportService, times(1)).addReport(any(ReportDto.class));
    }

    @Test
    void getReport_ShouldReturnReport() {
        when(reportService.getReport(reportId)).thenReturn(reportDto);
        ResponseEntity<ReportDto> response = reportController.getReport(reportId);

        assertNotNull(response.getBody());
        assertEquals(reportId, response.getBody().getId());
        verify(reportService, times(1)).getReport(reportId);
    }

    @Test
    void getReports_ShouldReturnReportList() {
        when(reportService.getReports()).thenReturn(Collections.singletonList(reportDto));
        ResponseEntity<List<ReportDto>> response = reportController.getReports();

        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        verify(reportService, times(1)).getReports();
    }

    @Test
    void updateReport_ShouldReturnUpdatedReport() {
        when(reportService.updateReport(eq(reportId), any(ReportDto.class))).thenReturn(reportDto);
        ResponseEntity<ReportDto> response = reportController.updateReport(reportId, reportDto);

        assertNotNull(response.getBody());
        assertEquals(reportId, response.getBody().getId());
        verify(reportService, times(1)).updateReport(eq(reportId), any(ReportDto.class));
    }

    @Test
    void deleteReport_ShouldReturnNoContent() {
        doNothing().when(reportService).deleteReport(reportId);
        ResponseEntity<Void> response = reportController.deleteReport(reportId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(reportService, times(1)).deleteReport(reportId);
    }
}

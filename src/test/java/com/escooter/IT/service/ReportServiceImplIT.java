package com.escooter.IT.service;

import com.escooter.dto.ReportDto;
import com.escooter.entity.Report;
import com.escooter.repository.ReportRepository;
import com.escooter.service.ReportService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReportServiceImplIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @BeforeAll
    static void setup() {
        System.setProperty("spring.datasource.url", postgres.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgres.getUsername());
        System.setProperty("spring.datasource.password", postgres.getPassword());
    }

    @Autowired
    private ReportService reportService;

    @Autowired
    private ReportRepository reportRepository;

    @AfterEach
    void cleanUp() {
        reportRepository.deleteAll();
        reportRepository.flush();
    }

    @Test
    void testAddReport() {
        ReportDto reportDto = new ReportDto(null, "Incident", LocalDateTime.now(), "{Details: Some issue}");

        ReportDto savedReport = reportService.addReport(reportDto);

        assertThat(savedReport).isNotNull();
        assertThat(savedReport.getId()).isNotNull();
        assertThat(savedReport.getReportType()).isEqualTo("Incident");
    }

    @Test
    void testGetReportById() {
        Report report = reportRepository.save(Report.builder()
                .reportType("Maintenance")
                .createdAt(LocalDateTime.now())
                .data("{\"task\": \"Fix brakes\"}")
                .build());

        ReportDto foundReport = reportService.getReport(report.getId());

        assertThat(foundReport).isNotNull();
        assertThat(foundReport.getReportType()).isEqualTo("Maintenance");
    }

    @Test
    void testUpdateReport() {
        Report report = reportRepository.save(Report.builder()
                .reportType("Damage")
                .createdAt(LocalDateTime.now())
                .data("{\"damage\": \"Broken wheel\"}")
                .build());

        ReportDto updateDto = new ReportDto(report.getId(), "Repaired", LocalDateTime.now(), "{\"status\": \"Fixed\"}");
        ReportDto updatedReport = reportService.updateReport(report.getId(), updateDto);

        assertThat(updatedReport.getReportType()).isEqualTo("Repaired");
        assertThat(updatedReport.getData()).contains("Fixed");
    }

    @Test
    void testDeleteReport() {
        Report report = reportRepository.save(Report.builder()
                .reportType("Theft")
                .createdAt(LocalDateTime.now())
                .data("{\"location\": \"Downtown\"}")
                .build());

        UUID reportId = report.getId();
        reportService.deleteReport(reportId);

        assertThatThrownBy(() -> reportService.getReport(reportId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void testGetAllReports() {
        reportRepository.save(Report.builder()
                .reportType("Inspection")
                .createdAt(LocalDateTime.now())
                .data("{\"status\": \"Passed\"}")
                .build());

        List<ReportDto> reports = reportService.getReports();

        assertThat(reports).isNotEmpty();
    }
}

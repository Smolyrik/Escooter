package com.escooter.controller;

import com.escooter.dto.ReportDto;
import com.escooter.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Validated
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<ReportDto> addReport(@Valid @RequestBody ReportDto reportDto) {
        return ResponseEntity.ok(reportService.addReport(reportDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportDto> getReport(@PathVariable UUID id) {
        return ResponseEntity.ok(reportService.getReport(id));
    }

    @GetMapping
    public ResponseEntity<List<ReportDto>> getReports() {
        return ResponseEntity.ok(reportService.getReports());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReportDto> updateReport(@PathVariable UUID id, @Valid @RequestBody ReportDto reportDto) {
        return ResponseEntity.ok(reportService.updateReport(id, reportDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable UUID id) {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }
}

package com.escooter.controller;

import com.escooter.dto.ReportDto;
import com.escooter.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Report Management", description = "Operations related to reports in the system")
@SecurityRequirement(name = "bearerAuth")
public class ReportController {

    private final ReportService reportService;

    @Operation(
            summary = "Add a new report",
            description = "Creates a new report with the given details.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully created report",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ReportDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            })
    @PostMapping
    public ResponseEntity<ReportDto> addReport(@Valid @RequestBody ReportDto reportDto) {
        return ResponseEntity.ok(reportService.addReport(reportDto));
    }

    @Operation(
            summary = "Get report by ID",
            description = "Fetches a report based on its unique ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved report",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ReportDto.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "Report not found")
            })
    @GetMapping("/{id}")
    public ResponseEntity<ReportDto> getReport(
            @Parameter(description = "Report ID") @PathVariable UUID id) {
        return ResponseEntity.ok(reportService.getReport(id));
    }

    @Operation(
            summary = "Get all reports",
            description = "Retrieves a list of all reports in the system.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of reports",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ReportDto.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            })
    @GetMapping
    public ResponseEntity<List<ReportDto>> getReports() {
        return ResponseEntity.ok(reportService.getReports());
    }

    @Operation(
            summary = "Update a report",
            description = "Updates an existing report identified by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully updated report",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ReportDto.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "Report not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data")
            })
    @PutMapping("/{id}")
    public ResponseEntity<ReportDto> updateReport(
            @Parameter(description = "Report ID") @PathVariable UUID id,
            @Valid @RequestBody ReportDto reportDto) {
        return ResponseEntity.ok(reportService.updateReport(id, reportDto));
    }

    @Operation(
            summary = "Delete a report",
            description = "Deletes a report identified by its ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully deleted report"),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "Report not found")
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(
            @Parameter(description = "Report ID") @PathVariable UUID id) {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }
}

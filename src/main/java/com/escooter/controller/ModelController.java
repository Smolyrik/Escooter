package com.escooter.controller;

import com.escooter.dto.ModelDto;
import com.escooter.service.ModelService;
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

@Slf4j
@RestController
@RequestMapping("/api/models")
@RequiredArgsConstructor
@Validated
@Tag(name = "Model Management", description = "Operations related to e-scooter models")
@SecurityRequirement(name = "bearerAuth")
public class ModelController {

    private final ModelService modelService;

    @Operation(
            summary = "Add a new model",
            description = "Adds a new e-scooter model to the system.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully added the model",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ModelDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            })
    @PostMapping
    public ResponseEntity<ModelDto> addModel(@Valid @RequestBody ModelDto modelDto) {
        return ResponseEntity.ok(modelService.addModel(modelDto));
    }

    @Operation(
            summary = "Get model by ID",
            description = "Fetches an e-scooter model by its unique ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully fetched the model",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ModelDto.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "Model not found")
            })
    @GetMapping("/{id}")
    public ResponseEntity<ModelDto> getModelById(@Parameter(description = "ID of the model") @PathVariable Integer id) {
        return ResponseEntity.ok(modelService.getModelById(id));
    }

    @Operation(
            summary = "Get all models",
            description = "Fetches a list of all e-scooter models.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully fetched the list of models",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ModelDto.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            })
    @GetMapping
    public ResponseEntity<List<ModelDto>> getAllModels() {
        return ResponseEntity.ok(modelService.getAllModels());
    }

    @Operation(
            summary = "Update model",
            description = "Updates details of an existing e-scooter model.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully updated the model",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ModelDto.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "Model not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            })
    @PutMapping("/{id}")
    public ResponseEntity<ModelDto> updateModel(
            @Parameter(description = "ID of the model") @PathVariable Integer id,
            @Valid @RequestBody ModelDto modelDto) {
        return ResponseEntity.ok(modelService.updateModel(id, modelDto));
    }

    @Operation(
            summary = "Delete model",
            description = "Deletes an e-scooter model by its unique ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully deleted the model"),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "Model not found")
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModel(@Parameter(description = "ID of the model") @PathVariable Integer id) {
        modelService.deleteModel(id);
        return ResponseEntity.noContent().build();
    }
}

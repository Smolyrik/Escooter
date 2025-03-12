package com.escooter.controller;

import com.escooter.dto.ModelDto;
import com.escooter.service.ModelService;
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
public class ModelController {

    private final ModelService modelService;

    @PostMapping
    public ResponseEntity<ModelDto> addModel(@Valid @RequestBody ModelDto modelDto) {
        return ResponseEntity.ok(modelService.addModel(modelDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModelDto> getModelById(@PathVariable Integer id) {
        return ResponseEntity.ok(modelService.getModelById(id));
    }

    @GetMapping
    public ResponseEntity<List<ModelDto>> getAllModels() {
        return ResponseEntity.ok(modelService.getAllModels());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ModelDto> updateModel(@PathVariable Integer id, @Valid @RequestBody ModelDto modelDto) {
        return ResponseEntity.ok(modelService.updateModel(id, modelDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModel(@PathVariable Integer id) {
        modelService.deleteModel(id);
        return ResponseEntity.noContent().build();
    }
}


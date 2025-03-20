package com.escooter.controller;

import com.escooter.dto.ModelDto;
import com.escooter.service.ModelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModelControllerTest {

    @Mock
    private ModelService modelService;

    @InjectMocks
    private ModelController modelController;

    private ModelDto modelDto;
    private Integer modelId;

    @BeforeEach
    void setUp() {
        modelId = 1;
        modelDto = ModelDto.builder().id(modelId).name("Scooter X").build();
    }

    @Test
    void addModel_ShouldReturnModel() {
        when(modelService.addModel(any(ModelDto.class))).thenReturn(modelDto);
        ResponseEntity<ModelDto> response = modelController.addModel(modelDto);

        assertNotNull(response.getBody());
        assertEquals(modelDto.getId(), response.getBody().getId());
        verify(modelService, times(1)).addModel(any(ModelDto.class));
    }

    @Test
    void getModelById_ShouldReturnModel() {
        when(modelService.getModelById(modelId)).thenReturn(modelDto);
        ResponseEntity<ModelDto> response = modelController.getModelById(modelId);

        assertNotNull(response.getBody());
        assertEquals(modelId, response.getBody().getId());
        verify(modelService, times(1)).getModelById(modelId);
    }

    @Test
    void getAllModels_ShouldReturnModelList() {
        when(modelService.getAllModels()).thenReturn(Collections.singletonList(modelDto));
        ResponseEntity<List<ModelDto>> response = modelController.getAllModels();

        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        verify(modelService, times(1)).getAllModels();
    }

    @Test
    void updateModel_ShouldReturnUpdatedModel() {
        when(modelService.updateModel(eq(modelId), any(ModelDto.class))).thenReturn(modelDto);
        ResponseEntity<ModelDto> response = modelController.updateModel(modelId, modelDto);

        assertNotNull(response.getBody());
        assertEquals(modelId, response.getBody().getId());
        verify(modelService, times(1)).updateModel(eq(modelId), any(ModelDto.class));
    }

    @Test
    void deleteModel_ShouldReturnNoContent() {
        doNothing().when(modelService).deleteModel(modelId);
        ResponseEntity<Void> response = modelController.deleteModel(modelId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(modelService, times(1)).deleteModel(modelId);
    }
}

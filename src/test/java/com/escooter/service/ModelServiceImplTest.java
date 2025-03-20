package com.escooter.service;

import com.escooter.dto.ModelDto;
import com.escooter.entity.Model;
import com.escooter.mapper.ModelMapper;
import com.escooter.repository.ModelRepository;
import com.escooter.service.impl.ModelServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModelServiceImplTest {

    @Mock
    private ModelRepository modelRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ModelServiceImpl modelService;

    private Model model;
    private ModelDto modelDto;
    private Integer modelId;

    @BeforeEach
    void setUp() {
        modelId = 1;
        model = Model.builder().id(modelId).name("Scooter X").build();
        modelDto = ModelDto.builder().id(modelId).name("Scooter X").build();
    }

    @Test
    void addModel_ShouldReturnModelDto() {
        when(modelMapper.toEntity(modelDto)).thenReturn(model);
        when(modelRepository.save(model)).thenReturn(model);
        when(modelMapper.toDto(model)).thenReturn(modelDto);

        ModelDto result = modelService.addModel(modelDto);

        assertNotNull(result);
        assertEquals(modelId, result.getId());
        assertEquals("Scooter X", result.getName());

        verify(modelRepository, times(1)).save(model);
    }

    @Test
    void getModelById_ShouldReturnModelDto_WhenModelExists() {
        when(modelRepository.findById(modelId)).thenReturn(Optional.of(model));
        when(modelMapper.toDto(model)).thenReturn(modelDto);

        ModelDto result = modelService.getModelById(modelId);

        assertNotNull(result);
        assertEquals(modelId, result.getId());
        assertEquals("Scooter X", result.getName());

        verify(modelRepository, times(1)).findById(modelId);
    }

    @Test
    void getModelById_ShouldThrowException_WhenModelNotFound() {
        when(modelRepository.findById(modelId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> modelService.getModelById(modelId));

        verify(modelRepository, times(1)).findById(modelId);
    }

    @Test
    void getAllModels_ShouldReturnModelDtoList() {
        when(modelRepository.findAll()).thenReturn(List.of(model));
        when(modelMapper.toDto(model)).thenReturn(modelDto);

        List<ModelDto> result = modelService.getAllModels();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Scooter X", result.getFirst().getName());

        verify(modelRepository, times(1)).findAll();
    }

    @Test
    void updateModel_ShouldReturnUpdatedModelDto_WhenModelExists() {
        when(modelRepository.existsById(modelId)).thenReturn(true);
        when(modelMapper.toEntity(modelDto)).thenReturn(model);
        when(modelRepository.save(model)).thenReturn(model);
        when(modelMapper.toDto(model)).thenReturn(modelDto);

        ModelDto result = modelService.updateModel(modelId, modelDto);

        assertNotNull(result);
        assertEquals(modelId, result.getId());
        assertEquals("Scooter X", result.getName());

        verify(modelRepository, times(1)).save(model);
    }

    @Test
    void updateModel_ShouldThrowException_WhenModelNotFound() {
        when(modelRepository.existsById(modelId)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> modelService.updateModel(modelId, modelDto));

        verify(modelRepository, times(0)).save(any());
    }

    @Test
    void deleteModel_ShouldDeleteModel_WhenModelExists() {
        when(modelRepository.existsById(modelId)).thenReturn(true);

        modelService.deleteModel(modelId);

        verify(modelRepository, times(1)).deleteById(modelId);
    }

    @Test
    void deleteModel_ShouldThrowException_WhenModelNotFound() {
        when(modelRepository.existsById(modelId)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> modelService.deleteModel(modelId));

        verify(modelRepository, times(0)).deleteById(any());
    }
}

package com.escooter.service.impl;

import com.escooter.dto.ModelDto;
import com.escooter.entity.Model;
import com.escooter.mapper.ModelMapper;
import com.escooter.repository.ModelRepository;
import com.escooter.service.ModelService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class ModelServiceImpl implements ModelService {

    private final ModelRepository modelRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public ModelDto addModel(ModelDto modelDto) {
        log.info("Attempting to add a new model: {}", modelDto);
        Model model = modelMapper.toEntity(modelDto);
        Model savedModel = modelRepository.save(model);
        log.info("Successfully added new model with ID: {}", savedModel.getId());
        return modelMapper.toDto(savedModel);
    }

    @Transactional(readOnly = true)
    public ModelDto getModelById(Integer id) {
        log.info("Fetching model with ID: {}", id);
        return modelRepository.findById(id)
                .map(modelMapper::toDto)
                .orElseThrow(() -> {
                    log.error("Model with ID: {} not found", id);
                    return new NoSuchElementException("Model with ID: " + id + " not found");
                });
    }

    @Transactional(readOnly = true)
    public List<ModelDto> getAllModels() {
        log.info("Fetching all models from database");
        List<ModelDto> models = modelRepository.findAll().stream()
                .map(modelMapper::toDto)
                .collect(Collectors.toList());
        log.info("Total models fetched: {}", models.size());
        return models;
    }

    @Transactional
    public ModelDto updateModel(Integer id, ModelDto modelDto) {
        log.info("Attempting to update model with ID: {}", id);
        if (!modelRepository.existsById(id)) {
            log.error("Model with ID: {} not found", id);
            throw new NoSuchElementException("Model with ID: " + id + " not found");
        }

        Model updatedModel = modelMapper.toEntity(modelDto);
        updatedModel.setId(id);

        Model savedModel = modelRepository.save(updatedModel);
        log.info("Successfully updated model with ID: {}", savedModel.getId());
        return modelMapper.toDto(savedModel);
    }

    @Transactional
    public void deleteModel(Integer id) {
        log.info("Attempting to delete model with ID: {}", id);
        if (!modelRepository.existsById(id)) {
            log.error("Model with ID: {} not found", id);
            throw new NoSuchElementException("Model with ID: " + id + " not found");
        }
        modelRepository.deleteById(id);
        log.info("Successfully deleted model with ID: {}", id);
    }
}

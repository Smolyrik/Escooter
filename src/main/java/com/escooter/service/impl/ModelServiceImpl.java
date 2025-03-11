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
        Model model = modelMapper.toEntity(modelDto);
        Model savedModel = modelRepository.save(model);
        log.info("Added new model with ID: {}", savedModel.getId());
        return modelMapper.toDto(savedModel);
    }

    @Transactional(readOnly = true)
    public ModelDto getModelById(Integer id) {
        return modelRepository.findById(id)
                .map(modelMapper::toDto)
                .orElseThrow(() -> {
                    log.error("Model with ID: {} not found", id);
                    return new NoSuchElementException("Model with ID: " + id + " not found");
                });
    }

    @Transactional(readOnly = true)
    public List<ModelDto> getAllModels() {
        log.info("Fetching all models");
        return modelRepository.findAll().stream()
                .map(modelMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ModelDto updateModel(Integer id, ModelDto modelDto) {
        if (!modelRepository.existsById(id)) {
            log.error("Model with ID: {} not found", id);
            throw new NoSuchElementException("Model with ID: " + id + " not found");
        }

        Model updatedModel = modelMapper.toEntity(modelDto);
        updatedModel.setId(id);

        Model savedModel = modelRepository.save(updatedModel);
        log.info("Updated model with ID: {}", savedModel.getId());

        return modelMapper.toDto(savedModel);
    }

    @Transactional
    public void deleteModel(Integer id) {
        if (!modelRepository.existsById(id)) {
            log.error("Model with ID: {} not found", id);
            throw new NoSuchElementException("Model with ID: " + id + " not found");
        }
        modelRepository.deleteById(id);
        log.info("Deleted model with ID: {}", id);
    }
}

package com.escooter.service;

import com.escooter.dto.ModelDto;

import java.util.List;

public interface ModelService {

    ModelDto addModel(ModelDto modelDto);

    ModelDto getModelById(Integer id);

    List<ModelDto> getAllModels();

    ModelDto updateModel(Integer id, ModelDto modelDto);

    void deleteModel(Integer id);

}

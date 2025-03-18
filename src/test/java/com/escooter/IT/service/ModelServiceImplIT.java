package com.escooter.IT.service;

import com.escooter.dto.ModelDto;
import com.escooter.entity.Model;
import com.escooter.repository.ModelRepository;
import com.escooter.service.ModelService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class ModelServiceImplIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.1")
            .withDatabaseName("testdb");

    @BeforeAll
    static void setup() {
        System.setProperty("spring.datasource.url", postgres.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgres.getUsername());
        System.setProperty("spring.datasource.password", postgres.getPassword());
    }

    @Autowired
    private ModelService modelService;

    @Autowired
    private ModelRepository modelRepository;

    @AfterEach
    void cleanUp() {
        modelRepository.deleteAll();
        modelRepository.flush();
    }

    @Test
    void testAddModel() {
        ModelDto modelDto = new ModelDto(null, "Xiaomi Pro 2");

        ModelDto savedModel = modelService.addModel(modelDto);

        assertThat(savedModel).isNotNull();
        assertThat(savedModel.getId()).isNotNull();
        assertThat(savedModel.getName()).isEqualTo("Xiaomi Pro 2");
    }

    @Test
    void testGetModelById() {
        Model model = modelRepository.save(new Model(null, "Segway Ninebot"));

        ModelDto foundModel = modelService.getModelById(model.getId());

        assertThat(foundModel).isNotNull();
        assertThat(foundModel.getName()).isEqualTo("Segway Ninebot");
    }

    @Test
    void testUpdateModel() {
        Model model = modelRepository.save(new Model(null, "Old Model"));

        ModelDto updateDto = new ModelDto(model.getId(), "Updated Model");
        ModelDto updatedModel = modelService.updateModel(model.getId(), updateDto);

        assertThat(updatedModel.getName()).isEqualTo("Updated Model");
    }

    @Test
    void testDeleteModel() {
        Model model = modelRepository.save(new Model(null, "To Be Deleted"));
        Integer modelId = model.getId();
        modelService.deleteModel(modelId);

        assertThatThrownBy(() -> modelService.getModelById(modelId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void testGetAllModels() {
        modelRepository.save(new Model(null, "Model A"));
        modelRepository.save(new Model(null, "Model B"));

        List<ModelDto> models = modelService.getAllModels();

        assertThat(models).isNotEmpty();
        assertThat(models.size()).isGreaterThanOrEqualTo(2);
    }
}

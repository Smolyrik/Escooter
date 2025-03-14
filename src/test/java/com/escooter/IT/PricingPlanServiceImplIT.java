package com.escooter.IT;

import com.escooter.dto.PricingPlanDto;
import com.escooter.entity.PricingPlan;
import com.escooter.repository.PricingPlanRepository;
import com.escooter.service.PricingPlanService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PricingPlanServiceImplIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @BeforeAll
    static void setup() {
        System.setProperty("spring.datasource.url", postgres.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgres.getUsername());
        System.setProperty("spring.datasource.password", postgres.getPassword());
    }

    @AfterAll
    static void teardown() {
        postgres.stop();
    }

    @Autowired
    private PricingPlanService pricingPlanService;

    @Autowired
    private PricingPlanRepository pricingPlanRepository;

    @AfterEach
    void cleanUp() {
        pricingPlanRepository.deleteAll();
        pricingPlanRepository.flush();
    }

    @Test
    void testAddPricingPlan() {
        PricingPlanDto pricingPlanDto = new PricingPlanDto(null, "Basic", new BigDecimal("10.00"), new BigDecimal("100.00"), new BigDecimal("5.00"));

        PricingPlanDto savedPlan = pricingPlanService.addPricingPlan(pricingPlanDto);

        assertThat(savedPlan).isNotNull();
        assertThat(savedPlan.getId()).isNotNull();
        assertThat(savedPlan.getName()).isEqualTo("Basic");
    }

    @Test
    void testGetPricingPlanById() {
        PricingPlan pricingPlan = pricingPlanRepository.save(PricingPlan.builder()
                .name("Premium")
                .pricePerHour(new BigDecimal("15.00"))
                .subscriptionPrice(new BigDecimal("150.00"))
                .discount(new BigDecimal("10.00"))
                .build());

        PricingPlanDto foundPlan = pricingPlanService.getPricingPlan(pricingPlan.getId());

        assertThat(foundPlan).isNotNull();
        assertThat(foundPlan.getName()).isEqualTo("Premium");
    }

    @Test
    void testUpdatePricingPlan() {
        PricingPlan pricingPlan = pricingPlanRepository.save(PricingPlan.builder()
                .name("Standard")
                .pricePerHour(new BigDecimal("12.00"))
                .subscriptionPrice(new BigDecimal("120.00"))
                .discount(new BigDecimal("7.00"))
                .build());

        PricingPlanDto updateDto = new PricingPlanDto(pricingPlan.getId(), "Updated Standard", new BigDecimal("13.00"), new BigDecimal("130.00"), new BigDecimal("8.00"));
        PricingPlanDto updatedPlan = pricingPlanService.updatePricingPlan(pricingPlan.getId(), updateDto);

        assertThat(updatedPlan.getName()).isEqualTo("Updated Standard");
        assertThat(updatedPlan.getPricePerHour()).isEqualTo(new BigDecimal("13.00"));
    }

    @Test
    void testDeletePricingPlan() {
        PricingPlan pricingPlan = pricingPlanRepository.save(PricingPlan.builder()
                .name("Special")
                .pricePerHour(new BigDecimal("9.00"))
                .subscriptionPrice(new BigDecimal("90.00"))
                .discount(new BigDecimal("4.00"))
                .build());

        UUID planId = pricingPlan.getId();
        pricingPlanService.deletePricingPlan(planId);

        assertThatThrownBy(() -> pricingPlanService.getPricingPlan(planId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void testGetAllPricingPlans() {
        pricingPlanRepository.save(PricingPlan.builder()
                .name("Enterprise")
                .pricePerHour(new BigDecimal("20.00"))
                .subscriptionPrice(new BigDecimal("200.00"))
                .discount(new BigDecimal("15.00"))
                .build());

        List<PricingPlanDto> plans = pricingPlanService.getAllPricingPlan();

        assertThat(plans).isNotEmpty();
    }
}

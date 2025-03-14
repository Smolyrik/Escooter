package com.escooter.controller;

import com.escooter.dto.PricingPlanDto;
import com.escooter.dto.ScooterDto;
import com.escooter.service.ScooterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScooterControllerTest {

    @Mock
    private ScooterService scooterService;

    @InjectMocks
    private ScooterController scooterController;

    private ScooterDto scooterDto;
    private UUID scooterId;

    @BeforeEach
    void setUp() {
        scooterId = UUID.randomUUID();

        scooterDto = ScooterDto.builder()
                .id(scooterId)
                .batteryLevel(new BigDecimal("85.5"))
                .mileage(new BigDecimal("120.0"))
                .build();
    }

    @Test
    void addScooter_ShouldReturnScooter() {
        when(scooterService.addScooter(any(ScooterDto.class))).thenReturn(scooterDto);
        ResponseEntity<ScooterDto> response = scooterController.addScooter(scooterDto);

        assertNotNull(response.getBody());
        assertEquals(scooterDto.getId(), response.getBody().getId());
        verify(scooterService, times(1)).addScooter(any(ScooterDto.class));
    }

    @Test
    void getScooterById_ShouldReturnScooter() {
        when(scooterService.getScooterById(scooterId)).thenReturn(scooterDto);
        ResponseEntity<ScooterDto> response = scooterController.getScooterById(scooterId);

        assertNotNull(response.getBody());
        assertEquals(scooterId, response.getBody().getId());
        verify(scooterService, times(1)).getScooterById(scooterId);
    }

    @Test
    void getAllScooters_ShouldReturnScooterList() {
        when(scooterService.getAllScooters()).thenReturn(Collections.singletonList(scooterDto));
        ResponseEntity<List<ScooterDto>> response = scooterController.getAllScooters();

        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        verify(scooterService, times(1)).getAllScooters();
    }

    @Test
    void updateScooter_ShouldReturnUpdatedScooter() {
        when(scooterService.updateScooter(eq(scooterId), any(ScooterDto.class))).thenReturn(scooterDto);
        ResponseEntity<ScooterDto> response = scooterController.updateScooter(scooterId, scooterDto);

        assertNotNull(response.getBody());
        assertEquals(scooterId, response.getBody().getId());
        verify(scooterService, times(1)).updateScooter(eq(scooterId), any(ScooterDto.class));
    }

    @Test
    void deleteScooter_ShouldReturnNoContent() {
        doNothing().when(scooterService).deleteScooter(scooterId);
        ResponseEntity<Void> response = scooterController.deleteScooter(scooterId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(scooterService, times(1)).deleteScooter(scooterId);
    }

    @Test
    void getPricingPlanByScooterId_ShouldReturnPricingPlan() {
        PricingPlanDto pricingPlanDto = new PricingPlanDto();
        when(scooterService.getPricingPlanByScooterId(scooterId)).thenReturn(pricingPlanDto);
        ResponseEntity<PricingPlanDto> response = scooterController.getPricingPlanByScooterId(scooterId);

        assertNotNull(response.getBody());
        verify(scooterService, times(1)).getPricingPlanByScooterId(scooterId);
    }
}

package com.escooter.controller;

import com.escooter.dto.PricingPlanDto;
import com.escooter.service.PricingPlanService;
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
class PricingPlanControllerTest {

    @Mock
    private PricingPlanService pricingPlanService;

    @InjectMocks
    private PricingPlanController pricingPlanController;

    private PricingPlanDto pricingPlanDto;
    private UUID planId;

    @BeforeEach
    void setUp() {
        planId = UUID.randomUUID();
        pricingPlanDto = PricingPlanDto.builder()
                .id(planId)
                .name("Standard Plan")
                .pricePerHour(new BigDecimal("5.00"))
                .subscriptionPrice(new BigDecimal("50.00"))
                .discount(new BigDecimal("10.00"))
                .build();
    }

    @Test
    void addPricingPlan_ShouldReturnPricingPlan() {
        when(pricingPlanService.addPricingPlan(any(PricingPlanDto.class))).thenReturn(pricingPlanDto);
        ResponseEntity<PricingPlanDto> response = pricingPlanController.addPricingPlan(pricingPlanDto);

        assertNotNull(response.getBody());
        assertEquals(pricingPlanDto.getId(), response.getBody().getId());
        verify(pricingPlanService, times(1)).addPricingPlan(any(PricingPlanDto.class));
    }

    @Test
    void getPricingPlan_ShouldReturnPricingPlan() {
        when(pricingPlanService.getPricingPlan(planId)).thenReturn(pricingPlanDto);
        ResponseEntity<PricingPlanDto> response = pricingPlanController.getPricingPlan(planId);

        assertNotNull(response.getBody());
        assertEquals(planId, response.getBody().getId());
        verify(pricingPlanService, times(1)).getPricingPlan(planId);
    }

    @Test
    void getAllPricingPlans_ShouldReturnPricingPlanList() {
        when(pricingPlanService.getAllPricingPlan()).thenReturn(Collections.singletonList(pricingPlanDto));
        ResponseEntity<List<PricingPlanDto>> response = pricingPlanController.getAllPricingPlans();

        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        verify(pricingPlanService, times(1)).getAllPricingPlan();
    }

    @Test
    void updatePricingPlan_ShouldReturnUpdatedPricingPlan() {
        when(pricingPlanService.updatePricingPlan(eq(planId), any(PricingPlanDto.class))).thenReturn(pricingPlanDto);
        ResponseEntity<PricingPlanDto> response = pricingPlanController.updatePricingPlan(planId, pricingPlanDto);

        assertNotNull(response.getBody());
        assertEquals(planId, response.getBody().getId());
        verify(pricingPlanService, times(1)).updatePricingPlan(eq(planId), any(PricingPlanDto.class));
    }

    @Test
    void deletePricingPlan_ShouldReturnNoContent() {
        doNothing().when(pricingPlanService).deletePricingPlan(planId);
        ResponseEntity<Void> response = pricingPlanController.deletePricingPlan(planId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(pricingPlanService, times(1)).deletePricingPlan(planId);
    }
}

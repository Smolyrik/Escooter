package com.escooter.service;

import com.escooter.dto.PricingPlanDto;
import com.escooter.entity.PricingPlan;
import com.escooter.mapper.PricingPlanMapper;
import com.escooter.repository.PricingPlanRepository;
import com.escooter.service.impl.PricingPlanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PricingPlanServiceImplTest {

    @Mock
    private PricingPlanRepository pricingPlanRepository;

    @Mock
    private PricingPlanMapper pricingPlanMapper;

    @InjectMocks
    private PricingPlanServiceImpl pricingPlanService;

    private PricingPlan pricingPlan;
    private PricingPlanDto pricingPlanDto;
    private UUID pricingPlanId;

    @BeforeEach
    void setUp() {
        pricingPlanId = UUID.randomUUID();
        pricingPlan = PricingPlan.builder()
                .id(pricingPlanId)
                .name("Standard Plan")
                .pricePerHour(BigDecimal.valueOf(5))
                .subscriptionPrice(BigDecimal.valueOf(50))
                .discount(BigDecimal.valueOf(10))
                .build();

        pricingPlanDto = PricingPlanDto.builder()
                .id(pricingPlanId)
                .name("Standard Plan")
                .pricePerHour(BigDecimal.valueOf(5))
                .subscriptionPrice(BigDecimal.valueOf(50))
                .discount(BigDecimal.valueOf(10))
                .build();
    }

    @Test
    void addPricingPlan_ShouldReturnPricingPlanDto() {
        when(pricingPlanMapper.toEntity(pricingPlanDto)).thenReturn(pricingPlan);
        when(pricingPlanRepository.save(pricingPlan)).thenReturn(pricingPlan);
        when(pricingPlanMapper.toDto(pricingPlan)).thenReturn(pricingPlanDto);

        PricingPlanDto result = pricingPlanService.addPricingPlan(pricingPlanDto);

        assertNotNull(result);
        assertEquals(pricingPlanId, result.getId());
        assertEquals("Standard Plan", result.getName());

        verify(pricingPlanRepository, times(1)).save(pricingPlan);
    }

    @Test
    void getPricingPlan_ShouldReturnPricingPlanDto_WhenPlanExists() {
        when(pricingPlanRepository.findById(pricingPlanId)).thenReturn(Optional.of(pricingPlan));
        when(pricingPlanMapper.toDto(pricingPlan)).thenReturn(pricingPlanDto);

        PricingPlanDto result = pricingPlanService.getPricingPlan(pricingPlanId);

        assertNotNull(result);
        assertEquals(pricingPlanId, result.getId());
        assertEquals("Standard Plan", result.getName());

        verify(pricingPlanRepository, times(1)).findById(pricingPlanId);
    }

    @Test
    void getPricingPlan_ShouldThrowException_WhenPlanNotFound() {
        when(pricingPlanRepository.findById(pricingPlanId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> pricingPlanService.getPricingPlan(pricingPlanId));

        verify(pricingPlanRepository, times(1)).findById(pricingPlanId);
    }

    @Test
    void getAllPricingPlans_ShouldReturnPricingPlanDtoList() {
        when(pricingPlanRepository.findAll()).thenReturn(List.of(pricingPlan));
        when(pricingPlanMapper.toDto(pricingPlan)).thenReturn(pricingPlanDto);

        List<PricingPlanDto> result = pricingPlanService.getAllPricingPlan();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Standard Plan", result.getFirst().getName());

        verify(pricingPlanRepository, times(1)).findAll();
    }

    @Test
    void updatePricingPlan_ShouldReturnUpdatedPricingPlanDto_WhenPlanExists() {
        when(pricingPlanRepository.existsById(pricingPlanId)).thenReturn(true);
        when(pricingPlanMapper.toEntity(pricingPlanDto)).thenReturn(pricingPlan);
        when(pricingPlanRepository.save(pricingPlan)).thenReturn(pricingPlan);
        when(pricingPlanMapper.toDto(pricingPlan)).thenReturn(pricingPlanDto);

        PricingPlanDto result = pricingPlanService.updatePricingPlan(pricingPlanId, pricingPlanDto);

        assertNotNull(result);
        assertEquals(pricingPlanId, result.getId());
        assertEquals("Standard Plan", result.getName());

        verify(pricingPlanRepository, times(1)).save(pricingPlan);
    }

    @Test
    void updatePricingPlan_ShouldThrowException_WhenPlanNotFound() {
        when(pricingPlanRepository.existsById(pricingPlanId)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> pricingPlanService.updatePricingPlan(pricingPlanId, pricingPlanDto));

        verify(pricingPlanRepository, times(0)).save(any());
    }

    @Test
    void deletePricingPlan_ShouldDeletePlan_WhenPlanExists() {
        when(pricingPlanRepository.existsById(pricingPlanId)).thenReturn(true);

        pricingPlanService.deletePricingPlan(pricingPlanId);

        verify(pricingPlanRepository, times(1)).deleteById(pricingPlanId);
    }

    @Test
    void deletePricingPlan_ShouldThrowException_WhenPlanNotFound() {
        when(pricingPlanRepository.existsById(pricingPlanId)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> pricingPlanService.deletePricingPlan(pricingPlanId));

        verify(pricingPlanRepository, times(0)).deleteById(any());
    }
}

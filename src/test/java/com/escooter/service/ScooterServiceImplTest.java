package com.escooter.service;

import com.escooter.dto.PricingPlanDto;
import com.escooter.dto.ScooterDto;
import com.escooter.entity.PricingPlan;
import com.escooter.entity.Scooter;
import com.escooter.mapper.PricingPlanMapper;
import com.escooter.mapper.ScooterMapper;
import com.escooter.repository.ScooterRepository;
import com.escooter.service.impl.ScooterServiceImpl;
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
class ScooterServiceImplTest {

    @Mock
    private ScooterRepository scooterRepository;

    @Mock
    private ScooterMapper scooterMapper;

    @Mock
    private PricingPlanMapper pricingPlanMapper;

    @InjectMocks
    private ScooterServiceImpl scooterService;

    private Scooter scooter;
    private ScooterDto scooterDto;
    private UUID scooterId;

    @BeforeEach
    void setUp() {
        scooterId = UUID.randomUUID();
        scooter = Scooter.builder()
                .id(scooterId)
                .batteryLevel(BigDecimal.valueOf(80))
                .mileage(BigDecimal.valueOf(120))
                .build();

        scooterDto = ScooterDto.builder()
                .id(scooterId)
                .batteryLevel(BigDecimal.valueOf(80))
                .mileage(BigDecimal.valueOf(120))
                .build();
    }

    @Test
    void addScooter_ShouldSaveAndReturnScooterDto() {
        when(scooterMapper.toEntity(scooterDto)).thenReturn(scooter);
        when(scooterRepository.save(scooter)).thenReturn(scooter);
        when(scooterMapper.toDto(scooter)).thenReturn(scooterDto);

        ScooterDto result = scooterService.addScooter(scooterDto);

        assertNotNull(result);
        assertEquals(scooterDto.getId(), result.getId());
        verify(scooterRepository, times(1)).save(scooter);
    }

    @Test
    void getScooterById_ShouldReturnScooterDto_WhenScooterExists() {
        when(scooterRepository.findById(scooterId)).thenReturn(Optional.of(scooter));
        when(scooterMapper.toDto(scooter)).thenReturn(scooterDto);

        ScooterDto result = scooterService.getScooterById(scooterId);

        assertNotNull(result);
        assertEquals(scooterDto.getId(), result.getId());
    }

    @Test
    void getScooterById_ShouldThrowException_WhenScooterNotFound() {
        when(scooterRepository.findById(scooterId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> scooterService.getScooterById(scooterId));
    }

    @Test
    void getAllScooters_ShouldReturnListOfScooters() {
        when(scooterRepository.findAll()).thenReturn(List.of(scooter));
        when(scooterMapper.toDto(scooter)).thenReturn(scooterDto);

        List<ScooterDto> result = scooterService.getAllScooters();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void updateScooter_ShouldUpdateAndReturnScooterDto() {
        when(scooterRepository.existsById(scooterId)).thenReturn(true);
        when(scooterMapper.toEntity(scooterDto)).thenReturn(scooter);
        when(scooterRepository.save(scooter)).thenReturn(scooter);
        when(scooterMapper.toDto(scooter)).thenReturn(scooterDto);

        ScooterDto result = scooterService.updateScooter(scooterId, scooterDto);

        assertNotNull(result);
        assertEquals(scooterDto.getId(), result.getId());
    }

    @Test
    void updateScooter_ShouldThrowException_WhenScooterNotFound() {
        when(scooterRepository.existsById(scooterId)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> scooterService.updateScooter(scooterId, scooterDto));
    }

    @Test
    void deleteScooter_ShouldDeleteScooter_WhenScooterExists() {
        when(scooterRepository.existsById(scooterId)).thenReturn(true);
        doNothing().when(scooterRepository).deleteById(scooterId);

        assertDoesNotThrow(() -> scooterService.deleteScooter(scooterId));
        verify(scooterRepository, times(1)).deleteById(scooterId);
    }

    @Test
    void deleteScooter_ShouldThrowException_WhenScooterNotFound() {
        when(scooterRepository.existsById(scooterId)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> scooterService.deleteScooter(scooterId));
    }

    @Test
    void getPricingPlanByScooterId_ShouldReturnPricingPlanDto_WhenScooterExists() {
        PricingPlan pricingPlan = PricingPlan.builder().id(UUID.randomUUID()).name("Basic Plan").build();
        PricingPlanDto pricingPlanDto = PricingPlanDto.builder().id(pricingPlan.getId()).name("Basic Plan").build();
        scooter.setPricingPlan(pricingPlan);

        when(scooterRepository.findById(scooterId)).thenReturn(Optional.of(scooter));
        when(pricingPlanMapper.toDto(pricingPlan)).thenReturn(pricingPlanDto);

        PricingPlanDto result = scooterService.getPricingPlanByScooterId(scooterId);

        assertNotNull(result);
        assertEquals(pricingPlanDto.getId(), result.getId());
    }

    @Test
    void getPricingPlanByScooterId_ShouldThrowException_WhenScooterNotFound() {
        when(scooterRepository.findById(scooterId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> scooterService.getPricingPlanByScooterId(scooterId));
    }
}

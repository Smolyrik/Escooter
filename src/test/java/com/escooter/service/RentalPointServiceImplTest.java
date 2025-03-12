package com.escooter.service;

import com.escooter.dto.RentalPointDto;
import com.escooter.dto.ScooterDto;
import com.escooter.entity.RentalPoint;
import com.escooter.entity.Scooter;
import com.escooter.mapper.RentalPointMapper;
import com.escooter.mapper.ScooterMapper;
import com.escooter.repository.RentalPointRepository;
import com.escooter.repository.ScooterRepository;
import com.escooter.service.impl.RentalPointServiceImpl;
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
class RentalPointServiceImplTest {

    @Mock
    private RentalPointRepository rentalPointRepository;

    @Mock
    private ScooterRepository scooterRepository;

    @Mock
    private RentalPointMapper rentalPointMapper;

    @Mock
    private ScooterMapper scooterMapper;

    @InjectMocks
    private RentalPointServiceImpl rentalPointService;

    private RentalPoint rentalPoint;
    private RentalPointDto rentalPointDto;
    private UUID rentalPointId;

    @BeforeEach
    void setUp() {
        rentalPointId = UUID.randomUUID();
        rentalPoint = RentalPoint.builder()
                .id(rentalPointId)
                .name("Test Point")
                .latitude(BigDecimal.valueOf(55.7558))
                .longitude(BigDecimal.valueOf(37.6173))
                .address("Test Address")
                .build();

        rentalPointDto = RentalPointDto.builder()
                .id(rentalPointId)
                .name("Test Point")
                .latitude(BigDecimal.valueOf(55.7558))
                .longitude(BigDecimal.valueOf(37.6173))
                .address("Test Address")
                .build();
    }

    @Test
    void addRentalPoint_ShouldReturnSavedRentalPointDto() {
        when(rentalPointMapper.toEntity(rentalPointDto)).thenReturn(rentalPoint);
        when(rentalPointRepository.save(rentalPoint)).thenReturn(rentalPoint);
        when(rentalPointMapper.toDto(rentalPoint)).thenReturn(rentalPointDto);

        RentalPointDto result = rentalPointService.addRentalPoint(rentalPointDto);

        assertNotNull(result);
        assertEquals(rentalPointId, result.getId());

        verify(rentalPointRepository, times(1)).save(rentalPoint);
    }

    @Test
    void getRentalPointById_ShouldReturnRentalPointDto() {
        when(rentalPointRepository.findById(rentalPointId)).thenReturn(Optional.of(rentalPoint));
        when(rentalPointMapper.toDto(rentalPoint)).thenReturn(rentalPointDto);

        RentalPointDto result = rentalPointService.getRentalPointById(rentalPointId);

        assertNotNull(result);
        assertEquals(rentalPointId, result.getId());

        verify(rentalPointRepository, times(1)).findById(rentalPointId);
    }

    @Test
    void getRentalPointById_ShouldThrowException_WhenNotFound() {
        when(rentalPointRepository.findById(rentalPointId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> rentalPointService.getRentalPointById(rentalPointId));

        verify(rentalPointRepository, times(1)).findById(rentalPointId);
    }

    @Test
    void getAllRentalPoints_ShouldReturnListOfRentalPoints() {
        when(rentalPointRepository.findAll()).thenReturn(List.of(rentalPoint));
        when(rentalPointMapper.toDto(rentalPoint)).thenReturn(rentalPointDto);

        List<RentalPointDto> result = rentalPointService.getAllRentalPoints();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(rentalPointId, result.getFirst().getId());

        verify(rentalPointRepository, times(1)).findAll();
    }

    @Test
    void updateRentalPoint_ShouldReturnUpdatedRentalPointDto() {
        when(rentalPointRepository.existsById(rentalPointId)).thenReturn(true);
        when(rentalPointMapper.toEntity(rentalPointDto)).thenReturn(rentalPoint);
        when(rentalPointRepository.save(rentalPoint)).thenReturn(rentalPoint);
        when(rentalPointMapper.toDto(rentalPoint)).thenReturn(rentalPointDto);

        RentalPointDto result = rentalPointService.updateRentalPoint(rentalPointId, rentalPointDto);

        assertNotNull(result);
        assertEquals(rentalPointId, result.getId());

        verify(rentalPointRepository, times(1)).existsById(rentalPointId);
        verify(rentalPointRepository, times(1)).save(rentalPoint);
    }

    @Test
    void updateRentalPoint_ShouldThrowException_WhenNotFound() {
        when(rentalPointRepository.existsById(rentalPointId)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> rentalPointService.updateRentalPoint(rentalPointId, rentalPointDto));

        verify(rentalPointRepository, times(1)).existsById(rentalPointId);
    }

    @Test
    void deleteRentalPoint_ShouldDeleteRentalPoint() {
        when(rentalPointRepository.existsById(rentalPointId)).thenReturn(true);

        rentalPointService.deleteRentalPoint(rentalPointId);

        verify(rentalPointRepository, times(1)).deleteById(rentalPointId);
    }

    @Test
    void deleteRentalPoint_ShouldThrowException_WhenNotFound() {
        when(rentalPointRepository.existsById(rentalPointId)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> rentalPointService.deleteRentalPoint(rentalPointId));

        verify(rentalPointRepository, times(1)).existsById(rentalPointId);
    }

    @Test
    void getAllScootersByRentalPoint_ShouldReturnListOfScooters() {
        UUID scooterId = UUID.randomUUID();
        ScooterDto scooterDto = ScooterDto.builder().id(scooterId).rentalPointId(rentalPointId).build();
        Scooter scooter = new Scooter();

        when(scooterRepository.findByRentalPointId(rentalPointId)).thenReturn(List.of(scooter));
        when(scooterMapper.toDto(scooter)).thenReturn(scooterDto);

        List<ScooterDto> result = rentalPointService.getAllScootersByRentalPoint(rentalPointId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(scooterId, result.getFirst().getId());

        verify(scooterRepository, times(1)).findByRentalPointId(rentalPointId);
    }


    @Test
    void getAvailableScootersByRentalPoint_ShouldReturnAvailableScooters() {
        when(scooterRepository.getAvailableScootersByRentalPointId(rentalPointId)).thenReturn(List.of());

        List<ScooterDto> result = rentalPointService.getAvailableScootersByRentalPoint(rentalPointId);

        assertNotNull(result);
        verify(scooterRepository, times(1)).getAvailableScootersByRentalPointId(rentalPointId);
    }

    @Test
    void getRentedScootersByRentalPoint_ShouldReturnRentedScooters() {
        when(scooterRepository.getRentedScootersByRentalPointId(rentalPointId)).thenReturn(List.of());

        List<ScooterDto> result = rentalPointService.getRentedScootersByRentalPoint(rentalPointId);

        assertNotNull(result);
        verify(scooterRepository, times(1)).getRentedScootersByRentalPointId(rentalPointId);
    }

    @Test
    void getScootersOnRepairByRentalPoint_ShouldReturnScootersOnRepair() {
        when(scooterRepository.getInRepairScootersByRentalPointId(rentalPointId)).thenReturn(List.of());

        List<ScooterDto> result = rentalPointService.getScootersOnRepairByRentalPoint(rentalPointId);

        assertNotNull(result);
        verify(scooterRepository, times(1)).getInRepairScootersByRentalPointId(rentalPointId);
    }
}

package com.escooter.controller;

import com.escooter.dto.RentalPointDto;
import com.escooter.dto.ScooterDto;
import com.escooter.service.RentalPointService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentalPointControllerTest {

    @Mock
    private RentalPointService rentalPointService;

    @InjectMocks
    private RentalPointController rentalPointController;

    private RentalPointDto rentalPointDto;
    private UUID rentalPointId;

    @BeforeEach
    void setUp() {
        rentalPointId = UUID.randomUUID();
        rentalPointDto = RentalPointDto.builder()
                .id(rentalPointId)
                .name("Test Point")
                .latitude(BigDecimal.valueOf(55.7558))
                .longitude(BigDecimal.valueOf(37.6173))
                .address("Test Address")
                .build();
    }

    @Test
    void addRentalPoint_ShouldReturnRentalPointDto() {
        when(rentalPointService.addRentalPoint(rentalPointDto)).thenReturn(rentalPointDto);

        ResponseEntity<RentalPointDto> response = rentalPointController.addRentalPoint(rentalPointDto);

        assertNotNull(response.getBody());
        assertEquals(rentalPointId, response.getBody().getId());
        verify(rentalPointService, times(1)).addRentalPoint(rentalPointDto);
    }

    @Test
    void getRentalPointById_ShouldReturnRentalPointDto() {
        when(rentalPointService.getRentalPointById(rentalPointId)).thenReturn(rentalPointDto);

        ResponseEntity<RentalPointDto> response = rentalPointController.getRentalPointById(rentalPointId);

        assertNotNull(response.getBody());
        assertEquals(rentalPointId, response.getBody().getId());
        verify(rentalPointService, times(1)).getRentalPointById(rentalPointId);
    }

    @Test
    void getAllRentalPoints_ShouldReturnListOfRentalPoints() {
        when(rentalPointService.getAllRentalPoints()).thenReturn(List.of(rentalPointDto));

        ResponseEntity<List<RentalPointDto>> response = rentalPointController.getAllRentalPoints();

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(rentalPointService, times(1)).getAllRentalPoints();
    }

    @Test
    void updateRentalPoint_ShouldReturnUpdatedRentalPointDto() {
        when(rentalPointService.updateRentalPoint(rentalPointId, rentalPointDto)).thenReturn(rentalPointDto);

        ResponseEntity<RentalPointDto> response = rentalPointController.updateRentalPoint(rentalPointId, rentalPointDto);

        assertNotNull(response.getBody());
        assertEquals(rentalPointId, response.getBody().getId());
        verify(rentalPointService, times(1)).updateRentalPoint(rentalPointId, rentalPointDto);
    }

    @Test
    void deleteRentalPoint_ShouldReturnNoContent() {
        doNothing().when(rentalPointService).deleteRentalPoint(rentalPointId);

        ResponseEntity<Void> response = rentalPointController.deleteRentalPoint(rentalPointId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(rentalPointService, times(1)).deleteRentalPoint(rentalPointId);
    }

    @Test
    void getAllScootersByRentalPoint_ShouldReturnListOfScooters() {
        ScooterDto scooterDto = ScooterDto.builder().id(UUID.randomUUID()).rentalPointId(rentalPointId).build();
        when(rentalPointService.getAllScootersByRentalPoint(rentalPointId)).thenReturn(List.of(scooterDto));

        ResponseEntity<List<ScooterDto>> response = rentalPointController.getAllScootersByRentalPoint(rentalPointId);

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(rentalPointService, times(1)).getAllScootersByRentalPoint(rentalPointId);
    }

    @Test
    void getAvailableScootersByRentalPoint_ShouldReturnListOfScooters() {
        ScooterDto scooterDto = ScooterDto.builder().id(UUID.randomUUID()).rentalPointId(rentalPointId).build();
        when(rentalPointService.getAvailableScootersByRentalPoint(rentalPointId)).thenReturn(List.of(scooterDto));

        ResponseEntity<List<ScooterDto>> response = rentalPointController.getAvailableScootersByRentalPoint(rentalPointId);

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(rentalPointService, times(1)).getAvailableScootersByRentalPoint(rentalPointId);
    }

    @Test
    void getRentedScootersByRentalPoint_ShouldReturnListOfScooters() {
        ScooterDto scooterDto = ScooterDto.builder().id(UUID.randomUUID()).rentalPointId(rentalPointId).build();
        when(rentalPointService.getRentedScootersByRentalPoint(rentalPointId)).thenReturn(List.of(scooterDto));

        ResponseEntity<List<ScooterDto>> response = rentalPointController.getRentedScootersByRentalPoint(rentalPointId);

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(rentalPointService, times(1)).getRentedScootersByRentalPoint(rentalPointId);
    }

    @Test
    void getScootersOnRepairByRentalPoint_ShouldReturnListOfScooters() {
        ScooterDto scooterDto = ScooterDto.builder().id(UUID.randomUUID()).rentalPointId(rentalPointId).build();
        when(rentalPointService.getScootersOnRepairByRentalPoint(rentalPointId)).thenReturn(List.of(scooterDto));

        ResponseEntity<List<ScooterDto>> response = rentalPointController.getScootersOnRepairByRentalPoint(rentalPointId);

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(rentalPointService, times(1)).getScootersOnRepairByRentalPoint(rentalPointId);
    }
}

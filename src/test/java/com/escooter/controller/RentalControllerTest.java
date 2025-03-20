package com.escooter.controller;

import com.escooter.dto.RentalDto;
import com.escooter.service.RentalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentalControllerTest {

    @Mock
    private RentalService rentalService;

    @InjectMocks
    private RentalController rentalController;

    private RentalDto rentalDto;
    private UUID rentalId;
    private UUID userId;
    private UUID scooterId;
    private Integer rentalTypeId;

    @BeforeEach
    void setUp() {
        rentalId = UUID.randomUUID();
        userId = UUID.randomUUID();
        scooterId = UUID.randomUUID();
        rentalTypeId = 1;
        rentalDto = RentalDto.builder()
                .id(rentalId)
                .userId(userId)
                .scooterId(scooterId)
                .build();
    }

    @Test
    void rentScooter_ShouldReturnRentalDto() {
        when(rentalService.rentScooter(userId, scooterId, rentalTypeId)).thenReturn(rentalDto);

        ResponseEntity<RentalDto> response = rentalController.rentScooter(userId, scooterId, rentalTypeId);

        assertNotNull(response.getBody());
        assertEquals(rentalId, response.getBody().getId());
        verify(rentalService, times(1)).rentScooter(userId, scooterId, rentalTypeId);
    }

    @Test
    void getAllRentals_ShouldReturnListOfRentals() {
        when(rentalService.getAllRentals()).thenReturn(List.of(rentalDto));

        ResponseEntity<List<RentalDto>> response = rentalController.getAllRentals();

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(rentalService, times(1)).getAllRentals();
    }

    @Test
    void endRental_ShouldReturnUpdatedRentalDto() {
        when(rentalService.endRental(rentalId, BigDecimal.TEN)).thenReturn(rentalDto);

        ResponseEntity<RentalDto> response = rentalController.endRental(rentalId, BigDecimal.TEN);

        assertNotNull(response.getBody());
        assertEquals(rentalId, response.getBody().getId());
        verify(rentalService, times(1)).endRental(rentalId, BigDecimal.TEN);
    }

    @Test
    void getRentalsByUserId_ShouldReturnUserRentals() {
        when(rentalService.getRentalsByUserId(userId)).thenReturn(List.of(rentalDto));

        ResponseEntity<List<RentalDto>> response = rentalController.getRentalsByUserId(userId);

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(rentalService, times(1)).getRentalsByUserId(userId);
    }

    @Test
    void getRentalsByScooterId_ShouldReturnScooterRentals() {
        when(rentalService.getRentalsByScooterId(scooterId)).thenReturn(List.of(rentalDto));

        ResponseEntity<List<RentalDto>> response = rentalController.getRentalsByScooterId(scooterId);

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(rentalService, times(1)).getRentalsByScooterId(scooterId);
    }
}

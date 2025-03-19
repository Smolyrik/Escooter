package com.escooter.service;

import com.escooter.dto.RentalDto;
import com.escooter.entity.*;
import com.escooter.mapper.RentalMapper;
import com.escooter.repository.*;
import com.escooter.service.impl.RentalServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentalServiceImplTest {

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ScooterRepository scooterRepository;

    @Mock
    private RentalStatusRepository rentalStatusRepository;

    @Mock
    private RentalMapper rentalMapper;

    @Mock
    private ScooterStatusRepository scooterStatusRepository;

    @Mock
    private RentalTypeRepository rentalTypeRepository;

    @InjectMocks
    private RentalServiceImpl rentalService;

    private UUID userId;
    private UUID scooterId;
    private UUID rentalId;
    private Integer rentalTypeId;
    private User user;
    private Scooter scooter;
    private Rental rental;
    private RentalDto rentalDto;
    private RentalType rentalType;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        scooterId = UUID.randomUUID();
        rentalId = UUID.randomUUID();
        rentalTypeId = 1;

        user = User.builder()
                .id(userId)
                .balance(new BigDecimal("100"))
                .build();
        scooter = Scooter.builder()
                .id(scooterId)
                .pricingPlan(PricingPlan.builder().pricePerHour(new BigDecimal("5.00")).build())
                .status(ScooterStatus.builder().name("AVAILABLE").build())
                .mileage(new BigDecimal("100"))
                .build();

        rentalType = RentalType.builder().id(rentalTypeId).name("HOURLY").build();
        RentalStatus activeStatus = RentalStatus.builder().name("ACTIVE").build();

        rental = Rental.builder()
                .id(rentalId)
                .user(user)
                .scooter(scooter)
                .startTime(LocalDateTime.now())
                .status(activeStatus)
                .rentalType(rentalType)
                .distance(BigDecimal.ZERO)
                .build();

        rentalDto = RentalDto.builder()
                .id(rentalId)
                .userId(userId)
                .scooterId(scooterId)
                .startTime(LocalDateTime.now())
                .statusId(1)
                .rentalTypeId(rentalTypeId)
                .distance(BigDecimal.ZERO)
                .build();
    }

    @Test
    void rentScooter_ShouldRentScooterSuccessfully() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(scooterRepository.findById(scooterId)).thenReturn(Optional.of(scooter));
        when(rentalTypeRepository.findById(rentalTypeId)).thenReturn(Optional.of(rentalType));
        when(scooterRepository.existsByIdAndStatus_Name(scooterId, "AVAILABLE")).thenReturn(true);
        when(scooterStatusRepository.findByName("RENTED")).thenReturn(Optional.of(new ScooterStatus(1, "RENTED")));
        when(rentalStatusRepository.findByName("ACTIVE")).thenReturn(Optional.of(new RentalStatus(2, "ACTIVE")));
        when(rentalRepository.save(any(Rental.class))).thenReturn(rental);
        when(rentalMapper.toDto(rental)).thenReturn(rentalDto);

        RentalDto result = rentalService.rentScooter(userId, scooterId, rentalTypeId);

        assertNotNull(result);
        assertEquals(rentalId, result.getId());
        verify(rentalRepository, times(1)).save(any(Rental.class));
    }

    @Test
    void rentScooter_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> rentalService.rentScooter(userId, scooterId, rentalTypeId));
    }

    @Test
    void rentScooter_ShouldThrowException_WhenScooterNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(scooterRepository.findById(scooterId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> rentalService.rentScooter(userId, scooterId, rentalTypeId));
    }

    @Test
    void rentScooter_ShouldThrowException_WhenRentalTypeNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(scooterRepository.findById(scooterId)).thenReturn(Optional.of(scooter));
        when(rentalTypeRepository.findById(rentalTypeId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> rentalService.rentScooter(userId, scooterId, rentalTypeId));
    }

    @Test
    void rentScooter_ShouldThrowException_WhenScooterAlreadyRented() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(scooterRepository.findById(scooterId)).thenReturn(Optional.of(scooter));
        when(scooterRepository.existsByIdAndStatus_Name(scooterId, "AVAILABLE")).thenReturn(false);
        when(rentalTypeRepository.findById(rentalTypeId)).thenReturn(Optional.of(rentalType));

        assertThrows(IllegalStateException.class, () -> rentalService.rentScooter(userId, scooterId, rentalTypeId));
    }

    @Test
    void endRental_ShouldCalculatePriceAndUpdateUserBalance() {
        rental.setStartTime(LocalDateTime.now().minusHours(2));
        rental.setDistance(BigDecimal.ZERO);

        RentalStatus completedStatus = new RentalStatus(null, "COMPLETED");
        ScooterStatus availableStatus = new ScooterStatus(null, "AVAILABLE");

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));
        when(rentalStatusRepository.findByName("COMPLETED")).thenReturn(Optional.of(completedStatus));
        when(scooterStatusRepository.findByName("AVAILABLE")).thenReturn(Optional.of(availableStatus));
        when(rentalRepository.save(any(Rental.class))).thenReturn(rental);
        when(rentalMapper.toDto(rental)).thenReturn(rentalDto);

        RentalDto result = rentalService.endRental(rentalId, BigDecimal.TEN);

        assertNotNull(result);
        verify(scooterRepository, times(1)).save(any(Scooter.class));
        verify(rentalRepository, times(1)).save(any(Rental.class));
    }

    @Test
    void endRental_ShouldThrowException_WhenRentalNotFound() {
        when(rentalRepository.findById(rentalId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> rentalService.endRental(rentalId, BigDecimal.TEN));
    }

    @Test
    void getRentalsByUserId_ShouldReturnRentalDtoList() {
        when(rentalRepository.findByUserId(userId)).thenReturn(List.of(rental));
        when(rentalMapper.toDto(rental)).thenReturn(rentalDto);

        List<RentalDto> result = rentalService.getRentalsByUserId(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getRentalsByScooterId_ShouldReturnRentalDtoList() {
        when(rentalRepository.findByScooterId(scooterId)).thenReturn(List.of(rental));
        when(rentalMapper.toDto(rental)).thenReturn(rentalDto);

        List<RentalDto> result = rentalService.getRentalsByScooterId(scooterId);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getAllRentals_ShouldReturnRentalDtoList() {
        when(rentalRepository.findAll()).thenReturn(List.of(rental));
        when(rentalMapper.toDto(rental)).thenReturn(rentalDto);

        List<RentalDto> result = rentalService.getAllRentals();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(rentalId, result.getFirst().getId());

        verify(rentalRepository, times(1)).findAll();
    }
}

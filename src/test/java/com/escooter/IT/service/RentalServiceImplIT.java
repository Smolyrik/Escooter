package com.escooter.IT.service;

import com.escooter.dto.RentalDto;
import com.escooter.entity.*;
import com.escooter.repository.*;
import com.escooter.service.RentalService;
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
@ActiveProfiles("test")
class RentalServiceImplIT {

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
    private ModelRepository modelRepository;
    @Autowired
    private RentalPointRepository rentalPointRepository;
    @Autowired
    private PricingPlanRepository pricingPlanRepository;
    @Autowired
    private RentalTypeRepository rentalTypeRepository;

    @Autowired
    private RentalService rentalService;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScooterRepository scooterRepository;

    @Autowired
    private RentalStatusRepository rentalStatusRepository;

    @Autowired
    private ScooterStatusRepository scooterStatusRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User testUser;
    private Scooter testScooter;
    private RentalType testRentalType;

    @BeforeEach
    void setUp() {
        Role testRole = roleRepository.findByName("USER")
                .orElse(new Role(null, "USER"));
        testUser = userRepository.save(User.builder()
                .role(testRole)
                .name("Test User")
                .email("testuser@example.com")
                .phone("+1234567890")
                .passwordHash("hashedpassword")
                .balance(new BigDecimal("100.00"))
                .build());

        RentalPoint testPoint = RentalPoint.builder()
                .name("Harbor Station")
                .latitude(new BigDecimal("40.7590"))
                .longitude(new BigDecimal("-73.9845"))
                .address("202 Ocean Dr")
                .manager(testUser)
                .build();

        PricingPlan testPlan = new PricingPlan(null, "Basic", new BigDecimal("10.00"), new BigDecimal("100.00"), new BigDecimal("5.00"));

        ScooterStatus availableStatus = scooterStatusRepository.findByName("AVAILABLE")
                .orElse(new ScooterStatus(null, "AVAILABLE"));

        testScooter = scooterRepository.save(Scooter.builder()
                .model(modelRepository.save(new Model(null, "Test Model")))
                .rentalPoint(rentalPointRepository.save(testPoint))
                .pricingPlan(pricingPlanRepository.save(testPlan))
                .status(availableStatus)
                .batteryLevel(new BigDecimal("80.00"))
                .mileage(new BigDecimal("100.00"))
                .build());

        testRentalType = rentalTypeRepository.findByName("HOURLY")
                .orElse(new RentalType(null, "HOURLY"));
    }

    @AfterEach
    void cleanUp() {
        rentalRepository.deleteAll();
        rentalRepository.flush();

        scooterRepository.deleteAll();
        scooterRepository.flush();

        pricingPlanRepository.deleteAll();
        pricingPlanRepository.flush();

        modelRepository.deleteAll();
        modelRepository.flush();

        rentalPointRepository.deleteAll();
        rentalPointRepository.flush();

        userRepository.deleteAll();
        userRepository.flush();
    }

    @Test
    void testRentScooter() {
        RentalDto rentalDto = rentalService.rentScooter(testUser.getId(), testScooter.getId(), testRentalType.getId());
        assertThat(rentalDto).isNotNull();
        assertThat(rentalDto.getUserId()).isEqualTo(testUser.getId());
        assertThat(rentalDto.getScooterId()).isEqualTo(testScooter.getId());
        assertThat(rentalDto.getStartTime()).isNotNull();
    }

    @Test
    void testRentScooterWithNegativeBalance() {
        testUser.setBalance(new BigDecimal("-10.00"));
        userRepository.save(testUser);

        assertThatThrownBy(() -> rentalService.rentScooter(testUser.getId(), testScooter.getId(), testRentalType.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("has a negative balance and cannot rent a scooter");
    }

    @Test
    void testEndRental() {
        RentalDto rentalDto = rentalService.rentScooter(testUser.getId(), testScooter.getId(), testRentalType.getId());
        RentalDto endedRental = rentalService.endRental(rentalDto.getId());
        assertThat(endedRental).isNotNull();
        assertThat(endedRental.getEndTime()).isNotNull();
        assertThat(endedRental.getTotalPrice()).isNotNull();
    }

    @Test
    void testGetAllRentals() {
        rentalService.rentScooter(testUser.getId(), testScooter.getId(), testRentalType.getId());
        List<RentalDto> rentals = rentalService.getAllRentals();

        assertThat(rentals).isNotEmpty();
    }

    @Test
    void testGetRentalsByUserId() {
        rentalService.rentScooter(testUser.getId(), testScooter.getId(), testRentalType.getId());
        List<RentalDto> rentals = rentalService.getRentalsByUserId(testUser.getId());

        assertThat(rentals).isNotEmpty();
    }

    @Test
    void testGetRentalsByScooterId() {
        rentalService.rentScooter(testUser.getId(), testScooter.getId(), testRentalType.getId());
        List<RentalDto> rentals = rentalService.getRentalsByScooterId(testScooter.getId());

        assertThat(rentals).isNotEmpty();
    }

    @Test
    void testRentScooterWhenAlreadyRented() {
        rentalService.rentScooter(testUser.getId(), testScooter.getId(), testRentalType.getId());

        assertThatThrownBy(() -> rentalService.rentScooter(testUser.getId(), testScooter.getId(), testRentalType.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("is already rented");
    }

    @Test
    void testEndRentalWhenNotFound() {
        UUID fakeRentalId = UUID.randomUUID();
        assertThatThrownBy(() -> rentalService.endRental(fakeRentalId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("not found");
    }
}

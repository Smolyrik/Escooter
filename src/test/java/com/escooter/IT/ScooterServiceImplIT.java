package com.escooter.IT;

import com.escooter.dto.ScooterDto;
import com.escooter.entity.*;
import com.escooter.repository.*;
import com.escooter.service.impl.ScooterServiceImpl;
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

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ScooterServiceImplIT {

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
    private ScooterServiceImpl scooterService;
    @Autowired
    private ScooterRepository scooterRepository;
    @Autowired
    private RentalPointRepository rentalPointRepository;
    @Autowired
    private ModelRepository modelRepository;
    @Autowired
    private PricingPlanRepository pricingPlanRepository;
    @Autowired
    private ScooterStatusRepository scooterStatusRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    UserRepository userRepository;

    private RentalPoint rentalPoint;
    private Model model;
    private PricingPlan pricingPlan;
    private ScooterStatus status;

    @BeforeEach
    void setUp() {
        Role testRole = roleRepository.save(new Role(null, "User"));
        User user = userRepository.save(User.builder()
                .role(testRole)
                .name("Alice")
                .email("alice@example.com")
                .phone("+9876543210")
                .passwordHash("hashedpass")
                .balance(new BigDecimal("50.00"))
                .build());
        rentalPoint = rentalPointRepository.save(new RentalPoint(null, "Central Point", BigDecimal.ZERO, BigDecimal.ZERO, "123 Main St", user));
        model = modelRepository.save(new Model(null, "Xiaomi M365"));
        pricingPlan = pricingPlanRepository.save(new PricingPlan(null, "Basic Plan", BigDecimal.valueOf(5), BigDecimal.valueOf(30), BigDecimal.valueOf(10)));
        status = scooterStatusRepository.save(new ScooterStatus(null, "Available"));
    }

    @AfterEach
    void cleanUp() {
        scooterRepository.deleteAll();
        scooterRepository.flush();

        rentalPointRepository.deleteAll();
        rentalPointRepository.flush();

        modelRepository.deleteAll();
        modelRepository.flush();

        pricingPlanRepository.deleteAll();
        pricingPlanRepository.flush();

        scooterStatusRepository.deleteAll();
        scooterStatusRepository.flush();

        userRepository.deleteAll();
        userRepository.flush();

        roleRepository.deleteAll();
        roleRepository.flush();
    }

    @Test
    void testAddScooter() {
        ScooterDto scooterDto = ScooterDto.builder()
                .rentalPointId(rentalPoint.getId())
                .modelId(model.getId())
                .batteryLevel(BigDecimal.valueOf(100))
                .pricingPlanId(pricingPlan.getId())
                .statusId(status.getId())
                .mileage(BigDecimal.ZERO)
                .build();

        ScooterDto savedScooter = scooterService.addScooter(scooterDto);
        assertNotNull(savedScooter.getId());
    }

    @Test
    void testGetScooterById() {
        Scooter scooter = new Scooter(null, rentalPoint, model, pricingPlan, BigDecimal.valueOf(90), status, BigDecimal.ZERO);
        scooter = scooterRepository.save(scooter);

        ScooterDto retrievedScooter = scooterService.getScooterById(scooter.getId());
        assertEquals(scooter.getId(), retrievedScooter.getId());
    }

    @Test
    void testUpdateScooter() {
        Scooter scooter = new Scooter(null, rentalPoint, model, pricingPlan, BigDecimal.valueOf(80), status, BigDecimal.ZERO);
        scooter = scooterRepository.save(scooter);

        ScooterDto updateDto = ScooterDto.builder()
                .id(scooter.getId())
                .rentalPointId(rentalPoint.getId())
                .modelId(model.getId())
                .batteryLevel(BigDecimal.valueOf(60))
                .pricingPlanId(pricingPlan.getId())
                .statusId(status.getId())
                .mileage(BigDecimal.TEN)
                .build();

        ScooterDto updatedScooter = scooterService.updateScooter(scooter.getId(), updateDto);
        assertEquals(BigDecimal.valueOf(60), updatedScooter.getBatteryLevel());
    }

    @Test
    void testDeleteScooter() {
        Scooter scooter = new Scooter(null, rentalPoint, model, pricingPlan, BigDecimal.valueOf(70), status, BigDecimal.ZERO);
        scooter = scooterRepository.save(scooter);

        scooterService.deleteScooter(scooter.getId());
        assertFalse(scooterRepository.existsById(scooter.getId()));
    }

    @Test
    void testGetPricingPlanByScooterId() {
        Scooter scooter = new Scooter(null, rentalPoint, model, pricingPlan, BigDecimal.valueOf(85), status, BigDecimal.ZERO);
        scooter = scooterRepository.save(scooter);

        var pricingPlanDto = scooterService.getPricingPlanByScooterId(scooter.getId());
        assertEquals(pricingPlan.getId(), pricingPlanDto.getId());
    }
}

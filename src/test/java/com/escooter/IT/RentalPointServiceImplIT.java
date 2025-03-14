package com.escooter.IT;

import com.escooter.dto.RentalPointDto;
import com.escooter.dto.ScooterDto;
import com.escooter.entity.RentalPoint;
import com.escooter.entity.Role;
import com.escooter.entity.User;
import com.escooter.repository.RentalPointRepository;
import com.escooter.repository.RoleRepository;
import com.escooter.repository.ScooterRepository;
import com.escooter.repository.UserRepository;
import com.escooter.service.RentalPointService;
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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RentalPointServiceImplIT {

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
    private RentalPointService rentalPointService;

    @Autowired
    private RentalPointRepository rentalPointRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ScooterRepository scooterRepository;

    private User testManager;

    @BeforeEach
    void setUp() {
        Role testRole = roleRepository.save(new Role(null, "User"));
        testManager = userRepository.save(User.builder()
                .role(testRole)
                .name("Manager")
                .email("manager@example.com")
                .phone("+1234567890")
                .passwordHash("hashedpass")
                .balance(new BigDecimal("100.00"))
                .build());
    }

    @AfterEach
    void cleanUp() {
        scooterRepository.deleteAll();
        rentalPointRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Nested
    class RentalPointTests {

        @Test
        void testAddRentalPoint() {
            RentalPointDto rentalPointDto = new RentalPointDto(null, "Downtown Station", new BigDecimal("40.7128"), new BigDecimal("-74.0060"), "123 Main St", testManager.getId());
            RentalPointDto savedRentalPoint = rentalPointService.addRentalPoint(rentalPointDto);

            assertThat(savedRentalPoint).isNotNull();
            assertThat(savedRentalPoint.getId()).isNotNull();
            assertThat(savedRentalPoint.getName()).isEqualTo("Downtown Station");
        }

        @Test
        void testGetRentalPointById() {
            RentalPoint rentalPoint = rentalPointRepository.save(RentalPoint.builder()
                    .name("Central Park Station")
                    .latitude(new BigDecimal("40.7851"))
                    .longitude(new BigDecimal("-73.9683"))
                    .address("456 Park Ave")
                    .manager(testManager)
                    .build());

            RentalPointDto foundRentalPoint = rentalPointService.getRentalPointById(rentalPoint.getId());

            assertThat(foundRentalPoint).isNotNull();
            assertThat(foundRentalPoint.getName()).isEqualTo("Central Park Station");
        }

        @Test
        void testUpdateRentalPoint() {
            RentalPoint rentalPoint = rentalPointRepository.save(RentalPoint.builder()
                    .name("Old Station")
                    .latitude(new BigDecimal("40.7000"))
                    .longitude(new BigDecimal("-74.0000"))
                    .address("789 Broadway")
                    .manager(testManager)
                    .build());

            RentalPointDto updateDto = new RentalPointDto(rentalPoint.getId(), "Updated Station", new BigDecimal("40.7010"), new BigDecimal("-74.0010"), "790 Broadway", testManager.getId());
            RentalPointDto updatedRentalPoint = rentalPointService.updateRentalPoint(rentalPoint.getId(), updateDto);

            assertThat(updatedRentalPoint.getName()).isEqualTo("Updated Station");
            assertThat(updatedRentalPoint.getAddress()).isEqualTo("790 Broadway");
        }

        @Test
        void testDeleteRentalPoint() {
            RentalPoint rentalPoint = rentalPointRepository.save(RentalPoint.builder()
                    .name("Temporary Station")
                    .latitude(new BigDecimal("40.7306"))
                    .longitude(new BigDecimal("-73.9352"))
                    .address("101 East St")
                    .manager(testManager)
                    .build());

            UUID rentalPointId = rentalPoint.getId();
            rentalPointService.deleteRentalPoint(rentalPointId);

            assertThatThrownBy(() -> rentalPointService.getRentalPointById(rentalPointId))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("not found");
        }

        @Test
        void testGetAllRentalPoints() {
            rentalPointRepository.save(RentalPoint.builder()
                    .name("Harbor Station")
                    .latitude(new BigDecimal("40.7590"))
                    .longitude(new BigDecimal("-73.9845"))
                    .address("202 Ocean Dr")
                    .manager(testManager)
                    .build());

            List<RentalPointDto> rentalPoints = rentalPointService.getAllRentalPoints();

            assertThat(rentalPoints).isNotEmpty();
        }
    }

    @Nested
    class ScooterTests {

        @Test
        void testGetAllScootersByRentalPoint() {
            RentalPoint rentalPoint = rentalPointRepository.save(RentalPoint.builder()
                    .name("Scooter Hub")
                    .latitude(new BigDecimal("40.7500"))
                    .longitude(new BigDecimal("-73.9900"))
                    .address("50 Scooter St")
                    .manager(testManager)
                    .build());

            List<ScooterDto> scooters = rentalPointService.getAllScootersByRentalPoint(rentalPoint.getId());

            assertThat(scooters).isNotNull();
        }

        @Test
        void testGetAvailableScootersByRentalPoint() {
            UUID rentalPointId = UUID.randomUUID();
            List<ScooterDto> availableScooters = rentalPointService.getAvailableScootersByRentalPoint(rentalPointId);

            assertThat(availableScooters).isNotNull();
        }

        @Test
        void testGetRentedScootersByRentalPoint() {
            UUID rentalPointId = UUID.randomUUID();
            List<ScooterDto> rentedScooters = rentalPointService.getRentedScootersByRentalPoint(rentalPointId);

            assertThat(rentedScooters).isNotNull();
        }

        @Test
        void testGetScootersOnRepairByRentalPoint() {
            UUID rentalPointId = UUID.randomUUID();
            List<ScooterDto> scootersOnRepair = rentalPointService.getScootersOnRepairByRentalPoint(rentalPointId);

            assertThat(scootersOnRepair).isNotNull();
        }
    }
}

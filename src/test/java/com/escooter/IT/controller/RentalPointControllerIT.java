package com.escooter.IT.controller;

import com.escooter.dto.RentalPointDto;
import com.escooter.dto.ScooterDto;
import com.escooter.entity.*;
import com.escooter.repository.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.security.Key;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RentalPointControllerIT {

    @LocalServerPort
    private int port;

    private WebClient webClient;

    @Autowired
    private RentalPointRepository rentalPointRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private PricingPlanRepository pricingPlanRepository;

    @Autowired
    private ScooterRepository scooterRepository;

    @Autowired
    private ScooterStatusRepository scooterStatusRepository;

    @Value("${token.signing.key}")
    private String jwtSigningKey;

    private User testUser;

    @BeforeEach
    void setUp() {
        String baseUrl = "http://localhost:" + port + "/rental-points";
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();

        Role testRole = roleRepository.save(new Role(null, "MANAGER"));
        testUser = userRepository.save(new User(null, testRole, "Test User", "test@example.com", "+1234567890", "hashedpassword", new BigDecimal("100.00")));
    }

    @AfterEach
    void cleanUp() {
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
        scooterStatusRepository.deleteAll();
        scooterStatusRepository.flush();
        roleRepository.deleteAll();
        roleRepository.flush();
    }

    @Test
    void testAddRentalPoint() {
        RentalPointDto rentalPointDto = new RentalPointDto(null, "Central Station", new BigDecimal("40.7128"), new BigDecimal("-74.0060"), "NYC", testUser.getId());

        RentalPointDto response = webClient.post()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(rentalPointDto)
                .retrieve()
                .bodyToMono(RentalPointDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Central Station");
    }

    @Test
    void testGetRentalPointById() {
        RentalPoint rentalPoint = rentalPointRepository.save(
                new RentalPoint(null, "Uptown", new BigDecimal("40.7851"), new BigDecimal("-73.9683"), "NYC", testUser)
        );

        RentalPointDto response = webClient.get()
                .uri("/" + rentalPoint.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(RentalPointDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Uptown");
    }

    @Test
    void testGetAllRentalPoints() {
        rentalPointRepository.save(new RentalPoint(null, "Central Station", new BigDecimal("40.7128"), new BigDecimal("-74.0060"), "NYC", testUser));

        RentalPointDto[] response = webClient.get()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(RentalPointDto[].class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.length).isGreaterThanOrEqualTo(1);
    }

    @Test
    void testUpdateRentalPoint() {
        RentalPoint rentalPoint = rentalPointRepository.save(
                new RentalPoint(null, "Old Name", new BigDecimal("40.7306"), new BigDecimal("-73.9352"), "NYC", testUser)
        );

        RentalPointDto updatedDto = new RentalPointDto(
                rentalPoint.getId(), "New Name", new BigDecimal("40.7306"), new BigDecimal("-73.9352"), "NYC", testUser.getId()
        );

        RentalPointDto response = webClient.put()
                .uri("/" + rentalPoint.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedDto)
                .retrieve()
                .bodyToMono(RentalPointDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("New Name");
    }

    @Test
    void testDeleteRentalPoint() {
        RentalPoint rentalPoint = rentalPointRepository.save(new RentalPoint(null, "Downtown", new BigDecimal("40.7306"), new BigDecimal("-73.9352"), "NYC", testUser));

        webClient.delete()
                .uri("/" + rentalPoint.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(Void.class)
                .block();

        assertThat(rentalPointRepository.findById(rentalPoint.getId())).isEmpty();
    }

    @Test
    void testGetAllScootersByRentalPoint() {
        RentalPoint rentalPoint = rentalPointRepository.save(
                new RentalPoint(null, "Scooter Station", new BigDecimal("40.7306"), new BigDecimal("-73.9352"), "NYC", testUser)
        );
        Model model = modelRepository.save(new Model(null, "Test Model"));
        PricingPlan pricingPlan = pricingPlanRepository.save(new PricingPlan(null, "Basic Plan", new BigDecimal("5.00"), new BigDecimal("50.00"), new BigDecimal("10.00")));
        ScooterStatus available = scooterStatusRepository.save(new ScooterStatus(null, "AVAILABLE"));
        scooterRepository.save(new Scooter(null, rentalPoint, model, pricingPlan, new BigDecimal(100), available, new BigDecimal(100)));

        ScooterDto[] response = webClient.get()
                .uri("/" + rentalPoint.getId() + "/scooters")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(ScooterDto[].class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.length).isGreaterThan(0);
    }

    @Test
    void testGetAvailableScootersByRentalPoint() {
        RentalPoint rentalPoint = rentalPointRepository.save(
                new RentalPoint(null, "Availability Test", new BigDecimal("40.7306"), new BigDecimal("-73.9352"), "NYC", testUser)
        );
        Model model = modelRepository.save(new Model(null, "Model X"));
        PricingPlan pricingPlan = pricingPlanRepository.save(new PricingPlan(null, "Standard", new BigDecimal("5.00"), new BigDecimal("50.00"), new BigDecimal("10.00")));
        ScooterStatus available = scooterStatusRepository.save(new ScooterStatus(null, "AVAILABLE"));
        scooterRepository.save(new Scooter(null, rentalPoint, model, pricingPlan, new BigDecimal(100), available, new BigDecimal(100)));

        ScooterDto[] response = webClient.get()
                .uri("/" + rentalPoint.getId() + "/scooters/available")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(ScooterDto[].class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.length).isGreaterThan(0);
    }

    @Test
    void testGetRentedScootersByRentalPoint() {
        RentalPoint rentalPoint = rentalPointRepository.save(new RentalPoint(null, "Rented Scooters", new BigDecimal("40.7306"), new BigDecimal("-73.9352"), "NYC", testUser));
        Model model = modelRepository.save(new Model(null, "Model Y"));
        PricingPlan pricingPlan = pricingPlanRepository.save(new PricingPlan(null, "Premium", new BigDecimal("10.00"), new BigDecimal("100.00"), new BigDecimal("20.00")));
        ScooterStatus rented = scooterStatusRepository.save(new ScooterStatus(null, "RENTED"));
        scooterRepository.save(new Scooter(null, rentalPoint, model, pricingPlan, new BigDecimal(100), rented, new BigDecimal(100)));

        ScooterDto[] response = webClient.get()
                .uri("/" + rentalPoint.getId() + "/scooters/rented")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(ScooterDto[].class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.length).isGreaterThan(0);
    }

    @Test
    void testGetScootersOnRepairByRentalPoint() {
        RentalPoint rentalPoint = rentalPointRepository.save(new RentalPoint(null, "Repair Station", new BigDecimal("40.7306"), new BigDecimal("-73.9352"), "NYC", testUser));
        Model model = modelRepository.save(new Model(null, "Model Z"));
        PricingPlan pricingPlan = pricingPlanRepository.save(new PricingPlan(null, "Deluxe", new BigDecimal("15.00"), new BigDecimal("150.00"), new BigDecimal("30.00")));
        ScooterStatus inRepair = scooterStatusRepository.save(new ScooterStatus(null, "IN REPAIR"));
        scooterRepository.save(new Scooter(null, rentalPoint, model, pricingPlan, new BigDecimal(100), inRepair, new BigDecimal(100)));

        ScooterDto[] response = webClient.get()
                .uri("/" + rentalPoint.getId() + "/scooters/repair")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(ScooterDto[].class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.length).isGreaterThan(0);
    }

    private String generateTestToken() {
        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSigningKey));

        return Jwts.builder()
                .subject("test@example.com")
                .claim("authorities", List.of("MANAGER"))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(key)
                .compact();
    }
}

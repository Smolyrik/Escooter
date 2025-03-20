package com.escooter.IT.controller;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.security.Key;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ScooterControllerIT {

    @LocalServerPort
    private int port;

    private WebClient webClient;

    @Autowired
    private ScooterRepository scooterRepository;

    @Autowired
    private RentalPointRepository rentalPointRepository;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PricingPlanRepository pricingPlanRepository;

    @Autowired
    private ScooterStatusRepository scooterStatusRepository;

    @Value("${token.signing.key}")
    private String jwtSigningKey;

    private RentalPoint rentalPoint;
    private Model model;
    private PricingPlan pricingPlan;
    private ScooterStatus scooterStatus;

    @BeforeEach
    void setUp() {
        String baseUrl = "http://localhost:" + port + "/api/scooters";
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();

        Role testRole = roleRepository.findByName("MANAGER")
                .orElse(new Role(null, "MANAGER"));
        User testUser = userRepository.save(new User(null, testRole, "Test User", "test@example.com", "+1234567890", "hashedpassword", new BigDecimal("100.00")));
        rentalPoint = rentalPointRepository.save(new RentalPoint(null, "Central Station", new BigDecimal("40.7128"), new BigDecimal("-74.0060"), "NYC", testUser));
        model = modelRepository.save(new Model(null, "Test Model"));
        pricingPlan = pricingPlanRepository.save(new PricingPlan(null, "Basic Plan", new BigDecimal("5.00"), new BigDecimal("50.00"), new BigDecimal("10.00")));
        scooterStatus = scooterStatusRepository.findByName("AVAILABLE")
                .orElse(new ScooterStatus(null, "AVAILABLE"));
    }

    @AfterEach
    void cleanUp() {
        scooterRepository.deleteAll();
        scooterRepository.flush();

        rentalPointRepository.deleteAll();
        rentalPointRepository.flush();

        userRepository.deleteAll();
        userRepository.flush();

        modelRepository.deleteAll();
        modelRepository.flush();

        pricingPlanRepository.deleteAll();
        pricingPlanRepository.flush();
    }

    @Test
    void testAddScooter() {
        ScooterDto scooterDto = new ScooterDto(null, rentalPoint.getId(), model.getId(), new BigDecimal("80"), pricingPlan.getId(), scooterStatus.getId(), new BigDecimal("100"));

        ScooterDto response = webClient.post()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(scooterDto)
                .retrieve()
                .bodyToMono(ScooterDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getBatteryLevel()).isEqualTo(new BigDecimal("80"));
    }

    @Test
    void testGetAllScooters() {
        scooterRepository.save(new Scooter(null, rentalPoint, model, pricingPlan, new BigDecimal("75"), scooterStatus, new BigDecimal("120")));
        scooterRepository.save(new Scooter(null, rentalPoint, model, pricingPlan, new BigDecimal("90"), scooterStatus, new BigDecimal("200")));

        ScooterDto[] response = webClient.get()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(ScooterDto[].class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.length).isGreaterThanOrEqualTo(2);
    }

    @Test
    void testUpdateScooter() {
        Scooter scooter = scooterRepository.save(new Scooter(null, rentalPoint, model, pricingPlan, new BigDecimal("50"), scooterStatus, new BigDecimal("300")));
        ScooterDto updatedScooter = new ScooterDto(scooter.getId(), rentalPoint.getId(), model.getId(), new BigDecimal("100"), pricingPlan.getId(), scooterStatus.getId(), new BigDecimal("350"));

        ScooterDto response = webClient.put()
                .uri("/" + scooter.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedScooter)
                .retrieve()
                .bodyToMono(ScooterDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getBatteryLevel()).isEqualTo(new BigDecimal("100"));
    }

    @Test
    void testDeleteScooter() {
        Scooter scooter = scooterRepository.save(new Scooter(null, rentalPoint, model, pricingPlan, new BigDecimal("30"), scooterStatus, new BigDecimal("400")));

        webClient.delete()
                .uri("/" + scooter.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(Void.class)
                .block();

        assertThat(scooterRepository.findById(scooter.getId())).isEmpty();
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

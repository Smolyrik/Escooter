package com.escooter.IT.controller;

import com.escooter.dto.RentalDto;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RentalControllerIT {

    @LocalServerPort
    private int port;

    private WebClient webClient;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScooterRepository scooterRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RentalPointRepository rentalPointRepository;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private PricingPlanRepository pricingPlanRepository;

    @Autowired
    private RentalStatusRepository rentalStatusRepository;

    @Autowired
    private ScooterStatusRepository scooterStatusRepository;

    @Value("${token.signing.key}")
    private String jwtSigningKey;

    private User testUser;
    private Scooter testScooter;
    private RentalStatus activeStatus;


    @BeforeEach
    void setUp() {
        String baseUrl = "http://localhost:" + port + "/rentals";
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();

        Role testRole = roleRepository.save(new Role(null, "MANAGER"));
        testUser = userRepository.save(new User(null, testRole, "Test User", "test@example.com", "+1234567890", "hashedpassword", new BigDecimal("100.00")));
        RentalPoint rentalPoint = rentalPointRepository.save(new RentalPoint(null, "Central Station", new BigDecimal("40.7128"), new BigDecimal("-74.0060"), "NYC", testUser));
        Model model = modelRepository.save(new Model(null, "Test Model"));
        PricingPlan pricingPlan = pricingPlanRepository.save(new PricingPlan(null, "Basic Plan", new BigDecimal("5.00"), new BigDecimal("50.00"), new BigDecimal("10.00")));
        activeStatus = rentalStatusRepository.save(new RentalStatus(null, "ACTIVE"));
        rentalStatusRepository.save(new RentalStatus(null, "COMPLETED"));
        ScooterStatus availableStatus = scooterStatusRepository.save(new ScooterStatus(null, "AVAILABLE"));
        scooterStatusRepository.save(new ScooterStatus(null, "RENTED"));
        testScooter = scooterRepository.save(new Scooter(null, rentalPoint, model, pricingPlan, new BigDecimal("100"), availableStatus, new BigDecimal("50")));

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
        rentalStatusRepository.deleteAll();
        rentalStatusRepository.flush();
        scooterStatusRepository.deleteAll();
        scooterStatusRepository.flush();
        roleRepository.deleteAll();
        roleRepository.flush();
    }

    @Test
    void testStartRental() {
        RentalDto response = webClient.post()
                .uri("/start?userId=" + testUser.getId() + "&scooterId=" + testScooter.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(RentalDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(testUser.getId());
        assertThat(response.getScooterId()).isEqualTo(testScooter.getId());
    }

    @Test
    void testGetAllRentals() {
        rentalRepository.save(new Rental(null, testUser, testScooter, activeStatus, LocalDateTime.now(), null, null, null));

        RentalDto[] response = webClient.get()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(RentalDto[].class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.length).isGreaterThanOrEqualTo(1);
    }

    @Test
    void testEndRental() {
        Rental rental = rentalRepository.save(new Rental(null, testUser, testScooter, activeStatus, LocalDateTime.now(), null, null, null));

        RentalDto response = webClient.post()
                .uri("/end?rentalId=" + rental.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(RentalDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getEndTime()).isNotNull();
    }

    @Test
    void testGetRentalsByUserId() {
        rentalRepository.save(new Rental(null, testUser, testScooter, activeStatus, LocalDateTime.now(), null, null, null));

        RentalDto[] response = webClient.get()
                .uri("/user/" + testUser.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(RentalDto[].class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.length).isGreaterThanOrEqualTo(1);
    }

    @Test
    void testGetRentalsByScooterId() {
        rentalRepository.save(new Rental(null, testUser, testScooter, activeStatus, LocalDateTime.now(), null, null, null));

        RentalDto[] response = webClient.get()
                .uri("/scooter/" + testScooter.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(RentalDto[].class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.length).isGreaterThanOrEqualTo(1);
    }

    private String generateTestToken() {
        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSigningKey));

        return Jwts.builder()
                .subject("test@example.com")
                .claim("authorities", List.of("USER"))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(key)
                .compact();
    }
}

package com.escooter.IT.controller;

import com.escooter.dto.PricingPlanDto;
import com.escooter.entity.PricingPlan;
import com.escooter.entity.Role;
import com.escooter.entity.User;
import com.escooter.repository.PricingPlanRepository;
import com.escooter.repository.RoleRepository;
import com.escooter.repository.UserRepository;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PricingPlanControllerIT {

    @LocalServerPort
    private int port;

    private WebClient webClient;

    @Autowired
    private PricingPlanRepository pricingPlanRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Value("${token.signing.key}")
    private String jwtSigningKey;

    @BeforeEach
    void setUp() {
        String baseUrl = "http://localhost:" + port + "/api/pricing-plans";
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();

        Role testRole = roleRepository.findByName("MANAGER")
                .orElse(new Role(null, "MANAGER"));
        userRepository.save(new User(null, testRole, "Test User", "test@example.com", "+1234567890", "hashedpassword", new BigDecimal("100.00")));
    }

    @AfterEach
    void cleanUp() {
        pricingPlanRepository.deleteAll();
        pricingPlanRepository.flush();

        userRepository.deleteAll();
        userRepository.flush();
    }

    @Test
    void testAddPricingPlan() {
        PricingPlanDto pricingPlan = new PricingPlanDto(null, "Basic Plan", new BigDecimal("5.00"), new BigDecimal("50.00"), new BigDecimal("10"));

        PricingPlanDto response = webClient.post()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(pricingPlan)
                .retrieve()
                .bodyToMono(PricingPlanDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Basic Plan");
    }

    @Test
    void testGetAllPricingPlans() {
        pricingPlanRepository.save(new PricingPlan(null, "Plan A", new BigDecimal("10.00"), new BigDecimal("100.00"), new BigDecimal("5")));
        pricingPlanRepository.save(new PricingPlan(null, "Plan B", new BigDecimal("20.00"), new BigDecimal("200.00"), new BigDecimal("10")));

        PricingPlanDto[] response = webClient.get()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(PricingPlanDto[].class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.length).isGreaterThanOrEqualTo(2);
    }

    @Test
    void testUpdatePricingPlan() {
        PricingPlan plan = pricingPlanRepository.save(new PricingPlan(null, "Old Plan", new BigDecimal("5.00"), new BigDecimal("50.00"), new BigDecimal("5")));

        PricingPlanDto updatedPlan = new PricingPlanDto(plan.getId(), "Updated Plan", new BigDecimal("7.00"), new BigDecimal("70.00"), new BigDecimal("7"));

        PricingPlanDto response = webClient.put()
                .uri("/" + plan.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedPlan)
                .retrieve()
                .bodyToMono(PricingPlanDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Updated Plan");
    }

    @Test
    void testDeletePricingPlan() {
        PricingPlan plan = pricingPlanRepository.save(new PricingPlan(null, "To Be Deleted", new BigDecimal("8.00"), new BigDecimal("80.00"), new BigDecimal("8")));
        UUID planId = plan.getId();

        webClient.delete()
                .uri("/" + planId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(Void.class)
                .block();

        assertThat(pricingPlanRepository.findById(planId)).isEmpty();
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

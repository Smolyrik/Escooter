package com.escooter.IT.controller;

import com.escooter.dto.PaymentDto;
import com.escooter.dto.PaymentStatusDto;
import com.escooter.entity.Payment;
import com.escooter.entity.PaymentStatus;
import com.escooter.entity.Role;
import com.escooter.entity.User;
import com.escooter.repository.PaymentRepository;
import com.escooter.repository.PaymentStatusRepository;
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
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PaymentControllerIT {

    @LocalServerPort
    private int port;

    private WebClient webClient;

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PaymentStatusRepository paymentStatusRepository;

    @Value("${token.signing.key}")
    private String jwtSigningKey;

    private User testUser;
    private PaymentStatus pendingStatus;

    @BeforeEach
    void setUp() {
        String baseUrl = "http://localhost:" + port + "/api/payments";
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();

        Role testRole = roleRepository.findByName("MANAGER")
                .orElse(new Role(null, "MANAGER"));
        testUser = userRepository.save(new User(null, testRole, "Test User", "test@example.com", "+1234567890", "hashedpassword", new BigDecimal("100.00")));
        pendingStatus = paymentStatusRepository.findByName("PENDING")
                .orElse(new PaymentStatus(null, "PENDING"));
    }

    @AfterEach
    void cleanUp() {
        paymentRepository.deleteAll();
        paymentRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();
    }

    @Test
    void testMakePayment() {
        PaymentDto response = webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/make").queryParam("userId", testUser.getId()).queryParam("amount", "50.00").build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(PaymentDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getAmount()).isEqualTo(new BigDecimal("50.00"));
    }

    @Test
    void testGetUserPayments() {
        paymentRepository.save(new Payment(null, testUser, new BigDecimal("20.00"), LocalDateTime.now(), pendingStatus));
        paymentRepository.save(new Payment(null, testUser, new BigDecimal("30.00"), LocalDateTime.now(), pendingStatus));

        PaymentDto[] response = webClient.get()
                .uri("/user/" + testUser.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(PaymentDto[].class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.length).isGreaterThanOrEqualTo(2);
    }

    @Test
    void testUpdatePaymentStatus() {
        Payment payment = paymentRepository.save(new Payment(null, testUser, new BigDecimal("40.00"), LocalDateTime.now(), pendingStatus));

        PaymentStatus completedStatus = paymentStatusRepository.findByName("COMPLETED")
                .orElseThrow(() -> new IllegalStateException("COMPLETED status not found"));

        PaymentStatusDto updatedStatus = new PaymentStatusDto(completedStatus.getId(), "COMPLETED");

        PaymentDto response = webClient.patch()
                .uri("/" + payment.getId() + "/status")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedStatus)
                .retrieve()
                .bodyToMono(PaymentDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getStatusId()).isEqualTo(completedStatus.getId());
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

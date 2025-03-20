package com.escooter.IT.controller;

import com.escooter.dto.ReportDto;
import com.escooter.entity.Report;
import com.escooter.entity.Role;
import com.escooter.entity.User;
import com.escooter.repository.ReportRepository;
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
class ReportControllerIT {

    @LocalServerPort
    private int port;

    private WebClient webClient;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Value("${token.signing.key}")
    private String jwtSigningKey;

    @BeforeEach
    void setUp() {
        String baseUrl = "http://localhost:" + port + "/api/reports";
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();

        Role testRole = roleRepository.findByName("MANAGER")
                .orElse(new Role(null, "MANAGER"));
        userRepository.save(new User(null, testRole, "Test User", "test@example.com", "+1234567890", "hashedpassword", new BigDecimal("100.00")));
    }

    @AfterEach
    void cleanUp() {
        reportRepository.deleteAll();
        reportRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();
    }

    @Test
    void testAddReport() {
        ReportDto report = new ReportDto(null, "Incident", LocalDateTime.now(), "Test report data");

        ReportDto response = webClient.post()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(report)
                .retrieve()
                .bodyToMono(ReportDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getReportType()).isEqualTo("Incident");
    }

    @Test
    void testGetAllReports() {
        reportRepository.save(new Report(null, "Incident", LocalDateTime.now(), "Sample Data"));
        reportRepository.save(new Report(null, "Maintenance", LocalDateTime.now(), "Another Sample Data"));

        ReportDto[] response = webClient.get()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(ReportDto[].class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.length).isGreaterThanOrEqualTo(2);
    }

    @Test
    void testUpdateReport() {
        Report report = reportRepository.save(new Report(null, "Incident", LocalDateTime.now(), "Initial Data"));

        ReportDto updatedReport = new ReportDto(report.getId(), "Updated Incident", report.getCreatedAt(), "Updated Data");

        ReportDto response = webClient.put()
                .uri("/" + report.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedReport)
                .retrieve()
                .bodyToMono(ReportDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getReportType()).isEqualTo("Updated Incident");
    }

    @Test
    void testDeleteReport() {
        Report report = reportRepository.save(new Report(null, "To Be Deleted", LocalDateTime.now(), "Data"));

        webClient.delete()
                .uri("/" + report.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(Void.class)
                .block();

        assertThat(reportRepository.findById(report.getId())).isEmpty();
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

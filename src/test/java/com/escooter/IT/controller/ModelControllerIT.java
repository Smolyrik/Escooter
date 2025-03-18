package com.escooter.IT.controller;

import com.escooter.dto.ModelDto;
import com.escooter.entity.Model;
import com.escooter.entity.Role;
import com.escooter.entity.User;
import com.escooter.repository.ModelRepository;
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

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ModelControllerIT {

    @LocalServerPort
    private int port;

    private WebClient webClient;

    @Autowired
    private ModelRepository modelRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Value("${token.signing.key}")
    private String jwtSigningKey;

    @BeforeEach
    void setUp() {
        String baseUrl = "http://localhost:" + port + "/api/models";
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();

        Role testRole = roleRepository.findByName("MANAGER")
                .orElse(new Role(null, "MANAGER"));
        userRepository.save(new User(null, testRole, "Test User", "test@example.com", "+1234567890", "hashedpassword", new BigDecimal("100.00")));
    }

    @AfterEach
    void cleanUp() {
        modelRepository.deleteAll();
        modelRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();
    }

    @Test
    void testAddModel() {
        ModelDto model = new ModelDto(null, "Test Model");

        ModelDto response = webClient.post()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(model)
                .retrieve()
                .bodyToMono(ModelDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Test Model");
    }

    @Test
    void testGetAllModels() {
        modelRepository.save(new Model(null, "Model A"));
        modelRepository.save(new Model(null, "Model B"));

        ModelDto[] response = webClient.get()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(ModelDto[].class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.length).isGreaterThanOrEqualTo(2);
    }

    @Test
    void testUpdateModel() {
        Model model = modelRepository.save(new Model(null, "Old Model"));
        ModelDto updatedModel = new ModelDto(model.getId(), "Updated Model");

        ModelDto response = webClient.put()
                .uri("/" + model.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedModel)
                .retrieve()
                .bodyToMono(ModelDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Updated Model");
    }

    @Test
    void testDeleteModel() {
        Model model = modelRepository.save(new Model(null, "To Be Deleted"));
        Integer modelId = model.getId();

        webClient.delete()
                .uri("/" + modelId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .toBodilessEntity()
                .block();

        assertThat(modelRepository.findById(modelId)).isEmpty();
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

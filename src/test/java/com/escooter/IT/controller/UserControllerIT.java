package com.escooter.IT.controller;

import com.escooter.dto.ChangePasswordRequest;
import com.escooter.dto.PartialUpdateUserRequest;
import com.escooter.dto.UserDto;
import com.escooter.entity.Role;
import com.escooter.entity.User;
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
import org.springframework.security.crypto.password.PasswordEncoder;
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
class UserControllerIT {

    @LocalServerPort
    private int port;

    private WebClient webClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Value("${token.signing.key}")
    private String jwtSigningKey;

    private Role testRole;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        String baseUrl = "http://localhost:" + port + "/api/users";
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();

        testRole = roleRepository.findByName("MANAGER")
                .orElse(new Role(null, "MANAGER"));
        userRepository.save(new User(null, testRole, "Test User", "test@example.com", "+1234567890", "hashedpassword", new BigDecimal("100.00")));
    }

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
        userRepository.flush();
    }

    @Test
    void testAddUser() {
        UserDto userDto = new UserDto(null, testRole.getId(), "New User", "new@example.com", "+9876543210", "securepassword", new BigDecimal("200.00"));

        UserDto response = webClient.post()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDto)
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    void testGetAllUsers() {
        userRepository.save(new User(null, testRole, "User A", "a@example.com", "+1111111111", "passwordA", new BigDecimal("150.00")));
        userRepository.save(new User(null, testRole, "User B", "b@example.com", "+2222222222", "passwordB", new BigDecimal("250.00")));

        UserDto[] response = webClient.get()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(UserDto[].class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.length).isGreaterThanOrEqualTo(2);
    }

    @Test
    void testUpdateUser() {
        User user = userRepository.save(new User(null, testRole, "Old User", "old@example.com", "+3333333333", "oldpassword", new BigDecimal("300.00")));
        UserDto updatedUser = new UserDto(user.getId(), testRole.getId(), "Updated User", "updated@example.com", "+4444444444", "newpassword", new BigDecimal("350.00"));

        UserDto response = webClient.put()
                .uri("/" + user.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedUser)
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    void testPartialUpdateUser() {
        User user = userRepository.save(new User(null, testRole, "Partial Update", "partial@example.com", "+6666666666", "partialpassword", new BigDecimal("500.00")));
        PartialUpdateUserRequest updateRequest = new PartialUpdateUserRequest();
        updateRequest.setName("Updated Name");
        updateRequest.setEmail("updated_partial@example.com");
        updateRequest.setPhone("+7777777777");

        UserDto response = webClient.patch()
                .uri("/" + user.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("updated_partial@example.com");
        assertThat(response.getName()).isEqualTo("Updated Name");
        assertThat(response.getPhone()).isEqualTo("+7777777777");
    }

    @Test
    void testChangePassword() {
        User user = userRepository.save(new User(null, testRole, "Password User", "password@example.com", "+8888888888", passwordEncoder.encode("oldpassword"), new BigDecimal("600.00")));
        ChangePasswordRequest passwordRequest = new ChangePasswordRequest();
        passwordRequest.setCurrentPassword("oldpassword");
        passwordRequest.setNewPassword("newsecurepassword");

        webClient.patch()
                .uri("/" + user.getId() + "/password")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(passwordRequest)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    @Test
    void testDeleteUser() {
        User user = userRepository.save(new User(null, testRole, "To Be Deleted", "delete@example.com", "+5555555555", "deletepassword", new BigDecimal("400.00")));

        webClient.delete()
                .uri("/" + user.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(Void.class)
                .block();

        assertThat(userRepository.findById(user.getId())).isEmpty();
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

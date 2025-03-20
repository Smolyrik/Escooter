package com.escooter.IT.service;

import com.escooter.dto.ChangePasswordRequest;
import com.escooter.dto.PartialUpdateUserRequest;
import com.escooter.dto.UserDto;
import com.escooter.entity.Role;
import com.escooter.entity.User;
import com.escooter.repository.RoleRepository;
import com.escooter.repository.UserRepository;
import com.escooter.service.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
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
@ActiveProfiles("test")
class UserServiceImplIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.1")
            .withDatabaseName("testdb");

    @BeforeAll
    static void setup() {
        System.setProperty("spring.datasource.url", postgres.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgres.getUsername());
        System.setProperty("spring.datasource.password", postgres.getPassword());
    }

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Role testRole;

    @BeforeEach
    void setUp() {
        testRole = roleRepository.findByName("USER")
                .orElse(new Role(null, "USER"));
    }

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
        userRepository.flush();
    }

    @Test
    void testAddUser() {
        UserDto userDto = new UserDto(null, testRole.getId(), "John Doe", "john@example.com", "+1234567890", "hashedpassword", new BigDecimal("100.00"));

        UserDto savedUser = userService.addUser(userDto);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void testGetUserById() {
        User user = userRepository.save(User.builder()
                .role(testRole)
                .name("Alice")
                .email("alice@example.com")
                .phone("+9876543210")
                .passwordHash("hashedpass")
                .balance(new BigDecimal("50.00"))
                .build());

        UserDto foundUser = userService.getUserById(user.getId());

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getName()).isEqualTo("Alice");
    }

    @Test
    void testUpdateUser() {
        User user = userRepository.save(User.builder()
                .role(testRole)
                .name("Bob")
                .email("bob@example.com")
                .phone("+1112233445")
                .passwordHash("hash123")
                .balance(new BigDecimal("30.00"))
                .build());

        UserDto updateDto = new UserDto(user.getId(), testRole.getId(), "Bobby", "bob@example.com", "+1112233445", "newhash", new BigDecimal("50.00"));

        UserDto updatedUser = userService.updateUser(user.getId(), updateDto);

        assertThat(updatedUser.getName()).isEqualTo("Bobby");
        assertThat(updatedUser.getBalance()).isEqualTo(new BigDecimal("50.00"));
    }

    @Test
    void testPartialUpdateUser() {
        User user = userRepository.save(User.builder()
                .role(testRole)
                .name("Eve")
                .email("eve@example.com")
                .phone("+123456789")
                .passwordHash("oldhash")
                .balance(new BigDecimal("40.00"))
                .build());

        PartialUpdateUserRequest updateRequest = new PartialUpdateUserRequest();
        updateRequest.setName("Eve Updated");
        updateRequest.setEmail("eve.updated@example.com");
        updateRequest.setPhone("+987654321");

        UserDto updatedUser = userService.partialUpdateUser(user.getId(), updateRequest);

        assertThat(updatedUser.getName()).isEqualTo("Eve Updated");
        assertThat(updatedUser.getEmail()).isEqualTo("eve.updated@example.com");
        assertThat(updatedUser.getPhone()).isEqualTo("+987654321");
    }

    @Test
    void testChangePassword() {
        User user = userRepository.save(User.builder()
                .role(testRole)
                .name("Frank")
                .email("frank@example.com")
                .phone("+111222333")
                .passwordHash(passwordEncoder.encode("oldpasswordhash"))
                .balance(new BigDecimal("60.00"))
                .build());

        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setCurrentPassword("oldpasswordhash");
        changePasswordRequest.setNewPassword("newpasswordhash");

        userService.changePassword(user.getId(), changePasswordRequest);
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();

        assertThat(passwordEncoder.matches("newpasswordhash", updatedUser.getPasswordHash())).isTrue();    }

    @Test
    void testDeleteUser() {
        User user = userRepository.save(User.builder()
                .role(testRole)
                .name("Charlie")
                .email("charlie@example.com")
                .phone("+5556667778")
                .passwordHash("securepass")
                .balance(new BigDecimal("75.00"))
                .build());

        UUID userId = user.getId();
        userService.deleteUser(userId);

        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void testGetAllUsers() {
        userRepository.save(User.builder()
                .role(testRole)
                .name("Dave")
                .email("dave@example.com")
                .phone("+9998887776")
                .passwordHash("hashxyz")
                .balance(new BigDecimal("20.00"))
                .build());

        List<UserDto> users = userService.getAllUsers();

        assertThat(users).isNotEmpty();
    }
}

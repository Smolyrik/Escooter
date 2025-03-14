package com.escooter.IT;

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
class UserServiceImplIT {

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
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Role testRole;

    @BeforeEach
    void setUp() {
        testRole = roleRepository.save(new Role(null, "User"));
    }

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
        userRepository.flush();
        roleRepository.deleteAll();
        roleRepository.flush();
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

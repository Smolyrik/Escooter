package com.escooter.IT;

import com.escooter.dto.PaymentDto;
import com.escooter.dto.PaymentStatusDto;
import com.escooter.entity.PaymentStatus;
import com.escooter.entity.Role;
import com.escooter.entity.User;
import com.escooter.repository.PaymentRepository;
import com.escooter.repository.PaymentStatusRepository;
import com.escooter.repository.RoleRepository;
import com.escooter.repository.UserRepository;
import com.escooter.service.PaymentService;
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

import static org.assertj.core.api.Assertions.*;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PaymentServiceImplIT {

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
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentStatusRepository paymentStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User testUser;
    private PaymentStatus pendingStatus;
    private PaymentStatus completedStatus;

    @BeforeEach
    void setUp() {
        Role userRole = roleRepository.save(new Role(null, "User"));
        testUser = userRepository.save(User.builder()
                .role(userRole)
                .name("Test User")
                .email("testuser@example.com")
                .phone("+123456789")
                .passwordHash("hashedpassword")
                .balance(new BigDecimal("200.00"))
                .build());

        pendingStatus = paymentStatusRepository.save(new PaymentStatus(null, "Pending"));
        completedStatus = paymentStatusRepository.save(new PaymentStatus(null, "Completed"));
    }

    @AfterEach
    void cleanUp() {
        paymentRepository.deleteAll();
        paymentStatusRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void testMakePayment() {
        BigDecimal amount = new BigDecimal("50.00");

        PaymentDto payment = paymentService.makePayment(testUser.getId(), amount);

        assertThat(payment).isNotNull();
        assertThat(payment.getId()).isNotNull();
        assertThat(payment.getUserId()).isEqualTo(testUser.getId());
        assertThat(payment.getAmount()).isEqualTo(amount);
        assertThat(payment.getStatusId()).isEqualTo(pendingStatus.getId());
    }

    @Test
    void testGetPaymentById() {
        PaymentDto payment = paymentService.makePayment(testUser.getId(), new BigDecimal("30.00"));

        PaymentDto foundPayment = paymentService.getPaymentById(payment.getId());

        assertThat(foundPayment).isNotNull();
        assertThat(foundPayment.getAmount()).isEqualTo(new BigDecimal("30.00"));
    }

    @Test
    void testUpdatePaymentStatus() {
        PaymentDto payment = paymentService.makePayment(testUser.getId(), new BigDecimal("40.00"));

        PaymentStatusDto newStatus = new PaymentStatusDto(completedStatus.getId(), "Completed");

        PaymentDto updatedPayment = paymentService.updatePaymentStatus(payment.getId(), newStatus);

        assertThat(updatedPayment.getStatusId()).isEqualTo(completedStatus.getId());
    }

    @Test
    void testGetUserPayments() {
        paymentService.makePayment(testUser.getId(), new BigDecimal("20.00"));
        paymentService.makePayment(testUser.getId(), new BigDecimal("25.00"));

        List<PaymentDto> payments = paymentService.getUserPayments(testUser.getId());

        assertThat(payments).hasSize(2);
    }
}

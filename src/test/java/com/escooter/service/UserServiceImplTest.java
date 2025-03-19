package com.escooter.service;

import com.escooter.dto.ChangePasswordRequest;
import com.escooter.dto.PartialUpdateUserRequest;
import com.escooter.dto.UserDto;
import com.escooter.entity.Role;
import com.escooter.entity.User;
import com.escooter.exception.InvalidPasswordException;
import com.escooter.mapper.UserMapper;
import com.escooter.repository.UserRepository;
import com.escooter.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        Role role = new Role(1, "User");

        user = User.builder()
                .id(userId)
                .role(role)
                .name("John Doe")
                .email("john.doe@example.com")
                .phone("+1234567890")
                .passwordHash("hashedPassword")
                .balance(new BigDecimal("100.00"))
                .build();

        userDto = UserDto.builder()
                .id(userId)
                .roleId(role.getId())
                .name("John Doe")
                .email("john.doe@example.com")
                .phone("+1234567890")
                .passwordHash("hashedPassword")
                .balance(new BigDecimal("100.00"))
                .build();
    }

    @Test
    void addUser_ShouldReturnUserDto() {
        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.addUser(userDto);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("+1234567890", result.getPhone());
        assertEquals(new BigDecimal("100.00"), result.getBalance());

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void getUserById_ShouldReturnUserDto_WhenUserExists() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals("+1234567890", result.getPhone());
        assertEquals(new BigDecimal("100.00"), result.getBalance());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userService.getUserById(userId));

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getAllUsers_ShouldReturnUserDtoList() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        List<UserDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.getFirst().getName());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void updateUser_ShouldReturnUpdatedUserDto_WhenUserExists() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.updateUser(userId, userDto);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("+1234567890", result.getPhone());
        assertEquals(new BigDecimal("100.00"), result.getBalance());

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> userService.updateUser(userId, userDto));

        verify(userRepository, times(0)).save(any());
    }

    @Test
    void partialUpdateUser_ShouldUpdateFields_WhenValidRequest() {
        PartialUpdateUserRequest updateRequest = new PartialUpdateUserRequest();
        updateRequest.setName("Jane Doe");
        updateRequest.setEmail("jane.doe@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.partialUpdateUser(userId, updateRequest);

        assertNotNull(result);
        assertEquals("Jane Doe", user.getName());
        assertEquals("jane.doe@example.com", user.getEmail());

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void partialUpdateUser_ShouldThrowException_WhenUserNotFound() {
        PartialUpdateUserRequest updateRequest = new PartialUpdateUserRequest();
        updateRequest.setName("Jane Doe");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userService.partialUpdateUser(userId, updateRequest));

        verify(userRepository, times(0)).save(any());
    }

    @Test
    void changePassword_ShouldChangePassword_WhenCurrentPasswordIsCorrect() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setNewPassword("newSecurePassword");
        request.setCurrentPassword("oldPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword", user.getPasswordHash())).thenReturn(true);
        when(passwordEncoder.encode("newSecurePassword")).thenReturn("encodedNewPassword");

        userService.changePassword(userId, request);

        assertEquals("encodedNewPassword", user.getPasswordHash());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void changePassword_ShouldThrowException_WhenUserNotFound() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setNewPassword("newSecurePassword");
        request.setCurrentPassword("oldPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userService.changePassword(userId, request));

        verify(userRepository, times(0)).save(any());
    }

    @Test
    void changePassword_ShouldThrowException_WhenCurrentPasswordIsIncorrect() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setNewPassword("newSecurePassword");
        request.setCurrentPassword("wrongPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", user.getPasswordHash())).thenReturn(false);

        assertThrows(InvalidPasswordException.class, () -> userService.changePassword(userId, request));

        verify(userRepository, times(0)).save(any());
    }

    @Test
    void deleteUser_ShouldDeleteUser_WhenUserExists() {
        when(userRepository.existsById(userId)).thenReturn(true);

        userService.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void deleteUser_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> userService.deleteUser(userId));

        verify(userRepository, times(0)).deleteById(any());
    }
}

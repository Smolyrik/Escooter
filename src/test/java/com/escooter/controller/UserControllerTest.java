package com.escooter.controller;

import com.escooter.dto.ChangePasswordRequest;
import com.escooter.dto.PartialUpdateUserRequest;
import com.escooter.dto.UserDto;
import com.escooter.entity.Role;
import com.escooter.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserDto userDto;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        Role role = new Role(1, "User");

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
    void addUser_ShouldReturnUser() {
        when(userService.addUser(any(UserDto.class))).thenReturn(userDto);
        ResponseEntity<UserDto> response = userController.addUser(userDto);

        assertNotNull(response.getBody());
        assertEquals(userDto.getId(), response.getBody().getId());
        verify(userService, times(1)).addUser(any(UserDto.class));
    }

    @Test
    void getUserById_ShouldReturnUser() {
        when(userService.getUserById(userId)).thenReturn(userDto);
        ResponseEntity<UserDto> response = userController.getUserById(userId);

        assertNotNull(response.getBody());
        assertEquals(userId, response.getBody().getId());
        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    void getAllUsers_ShouldReturnUserList() {
        when(userService.getAllUsers()).thenReturn(Collections.singletonList(userDto));
        ResponseEntity<List<UserDto>> response = userController.getAllUsers();

        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() {
        when(userService.updateUser(eq(userId), any(UserDto.class))).thenReturn(userDto);
        ResponseEntity<UserDto> response = userController.updateUser(userId, userDto);

        assertNotNull(response.getBody());
        assertEquals(userId, response.getBody().getId());
        verify(userService, times(1)).updateUser(eq(userId), any(UserDto.class));
    }

    @Test
    void updateUserPartially_ShouldReturnUpdatedUser() {
        PartialUpdateUserRequest request = new PartialUpdateUserRequest();
        request.setName("Updated Name");
        request.setEmail("updated.email@example.com");
        request.setPhone("+9876543210");

        when(userService.partialUpdateUser(eq(userId), any(PartialUpdateUserRequest.class)))
                .thenReturn(userDto);

        ResponseEntity<UserDto> response = userController.updateUserPartially(userId, request);

        assertNotNull(response.getBody());
        assertEquals(userId, response.getBody().getId());
        verify(userService, times(1)).partialUpdateUser(eq(userId), any(PartialUpdateUserRequest.class));
    }

    @Test
    void changePassword_ShouldReturnNoContent() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("oldPassword");
        request.setNewPassword("newSecurePassword");

        doNothing().when(userService).changePassword(eq(userId), any(ChangePasswordRequest.class));

        ResponseEntity<Void> response = userController.changePassword(userId, request);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService, times(1)).changePassword(eq(userId), any(ChangePasswordRequest.class));
    }

    @Test
    void deleteUser_ShouldReturnNoContent() {
        doNothing().when(userService).deleteUser(userId);
        ResponseEntity<Void> response = userController.deleteUser(userId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService, times(1)).deleteUser(userId);
    }
}

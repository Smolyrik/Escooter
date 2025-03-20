package com.escooter.service.impl;

import com.escooter.dto.ChangePasswordRequest;
import com.escooter.dto.PartialUpdateUserRequest;
import com.escooter.dto.UserDto;
import com.escooter.entity.User;
import com.escooter.exception.InvalidPasswordException;
import com.escooter.mapper.UserMapper;
import com.escooter.repository.UserRepository;
import com.escooter.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDto addUser(UserDto userDto) {
        log.info("Adding a new user");
        User user = userMapper.toEntity(userDto);
        User savedUser = userRepository.save(user);
        log.info("Added new user with ID: {}", savedUser.getId());
        return userMapper.toDto(savedUser);
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(UUID userId) {
        log.info("Fetching user with ID: {}", userId);
        return userRepository.findById(userId)
                .map(userMapper::toDto)
                .orElseThrow(() -> {
                    log.error("User with ID: {} not found", userId);
                    return new NoSuchElementException("User with ID: " + userId + " not found");
                });
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDto updateUser(UUID userId, UserDto userDto) {
        log.info("Updating user with ID: {}", userId);
        if (!userRepository.existsById(userId)) {
            log.error("User with ID: {} not found", userId);
            throw new NoSuchElementException("User with ID: " + userId + " not found");
        }

        User updatedUser = userMapper.toEntity(userDto);
        updatedUser.setId(userId);
        User savedUser = userRepository.save(updatedUser);
        log.info("Updated user with ID: {}", savedUser.getId());

        return userMapper.toDto(savedUser);
    }

    @Transactional
    public UserDto partialUpdateUser(UUID userId, PartialUpdateUserRequest partialUpdateUserRequest) {
        log.info("Partially updating user with ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User with ID: {} not found", userId);
                    return new NoSuchElementException("User with ID: " + userId + " not found");
                });

        if (partialUpdateUserRequest.getName() != null) {
            log.info("Updating name for user ID: {}", userId);
            user.setName(partialUpdateUserRequest.getName());
        }
        if (partialUpdateUserRequest.getEmail() != null) {
            log.info("Updating email for user ID: {}", userId);
            user.setEmail(partialUpdateUserRequest.getEmail());
        }
        if (partialUpdateUserRequest.getPhone() != null) {
            log.info("Updating phone for user ID: {}", userId);
            user.setPhone(partialUpdateUserRequest.getPhone());
        }

        userRepository.save(user);
        log.info("Successfully partially updated user with ID: {}", userId);
        return userMapper.toDto(user);
    }

    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        log.info("Attempting to change password for user with ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User with ID: {} not found", userId);
                    return new NoSuchElementException("User with ID: " + userId + " not found");
                });

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            log.warn("Incorrect current password provided for user ID: {}", userId);
            throw new InvalidPasswordException("Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password successfully changed for user ID: {}", userId);
    }

    @Transactional
    public void deleteUser(UUID userId) {
        log.info("Deleting user with ID: {}", userId);
        if (!userRepository.existsById(userId)) {
            log.error("User with ID: {} not found", userId);
            throw new NoSuchElementException("User with ID: " + userId + " not found");
        }
        userRepository.deleteById(userId);
        log.info("Deleted user with ID: {}", userId);
    }
}

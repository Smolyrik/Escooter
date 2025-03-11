package com.escooter.service.impl;


import com.escooter.dto.UserDto;
import com.escooter.entity.User;
import com.escooter.mapper.UserMapper;
import com.escooter.repository.UserRepository;
import com.escooter.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Transactional
    public UserDto addUser(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        User savedUser = userRepository.save(user);
        log.info("Added new user with ID: {}", savedUser.getId());
        return userMapper.toDto(savedUser);
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(UUID userId) {
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
        if (!userRepository.existsById(userId)) {
            log.error("User with ID: {} not found", userId);
            throw  new NoSuchElementException("User with ID: " + userId + " not found");
        }

        User updatedUser = userMapper.toEntity(userDto);
        updatedUser.setId(userId);

        User savedUser = userRepository.save(updatedUser);
        log.info("Updated user with ID: {}", savedUser.getId());

        return userMapper.toDto(savedUser);
    }

    @Transactional
    public void deleteUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
            log.error("User with ID: {} not found", userId);
            throw new NoSuchElementException("User with ID: " + userId + " not found");
        }
        userRepository.deleteById(userId);
        log.info("Deleted user with ID: {}", userId);
    }
}

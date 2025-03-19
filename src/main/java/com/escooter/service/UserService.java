package com.escooter.service;


import com.escooter.dto.ChangePasswordRequest;
import com.escooter.dto.PartialUpdateUserRequest;
import com.escooter.dto.UserDto;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserDto addUser(UserDto userDto);

    UserDto getUserById(UUID userId);

    List<UserDto> getAllUsers();

    UserDto updateUser(UUID userId, UserDto userDto);

    UserDto partialUpdateUser(UUID userId, PartialUpdateUserRequest updateUserRequest);

    void changePassword(UUID userId, ChangePasswordRequest request);

    void deleteUser(UUID userId);

}

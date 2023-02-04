package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    UserResponseDto getUserById(Long userId);

    List<UserResponseDto> getUsers();

    UserResponseDto addUser(UserRequestDto user);

    UserResponseDto updateUser(Long userId, UserRequestDto user);

    void deleteUser(Long userId);
}

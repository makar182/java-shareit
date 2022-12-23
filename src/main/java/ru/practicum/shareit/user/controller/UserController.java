package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.interfaces.OnAdd;
import ru.practicum.shareit.interfaces.OnUpdate;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserResponseDto> getUsers() {
        return UserMapper.toDtoList(userService.getUsers());
    }

    @GetMapping("/{userId}")
    public UserResponseDto getUserById(@PathVariable("userId") Long userId) {
        return UserMapper.toDto(userService.getUserById(userId));
    }

    @PostMapping
    public UserResponseDto addUser(@Validated(OnAdd.class) @RequestBody UserRequestDto userRequestDto) {
        return UserMapper.toDto(userService.addUser(UserMapper.toEntity(userRequestDto)));
    }

    @PatchMapping("/{userId}")
    public UserResponseDto updateUser(@PathVariable("userId") Long userId, @Validated(OnUpdate.class) @RequestBody UserRequestDto userRequestDto) {
        return UserMapper.toDto(userService.updateUser(userId, UserMapper.toEntity(userRequestDto)));
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") Long userId) {
        userService.deleteUser(userId);
    }
}

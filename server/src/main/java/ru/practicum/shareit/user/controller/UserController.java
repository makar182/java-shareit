package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserResponseDto> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    public UserResponseDto getUserById(@PathVariable("userId") Long userId) {
        return userService.getUserById(userId);
    }

    @PostMapping
    public UserResponseDto addUser(@RequestBody UserRequestDto userRequestDto) {
        return userService.addUser(userRequestDto);
    }

    @PatchMapping("/{userId}")
    public UserResponseDto updateUser(@PathVariable("userId") Long userId, @RequestBody UserRequestDto userRequestDto) {
        return userService.updateUser(userId, userRequestDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") Long userId) {
        userService.deleteUser(userId);
    }
}

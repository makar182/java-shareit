package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

public class UserMapper {
    public static UserResponseDto toDto(User dto) {
        return UserResponseDto.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }

    public static User toEntity(UserRequestDto dto) {
        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }
}

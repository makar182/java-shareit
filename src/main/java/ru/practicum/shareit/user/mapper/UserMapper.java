package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {
    public static UserResponseDto toDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static List<UserResponseDto> toDtoList(List<User> users) {
        List<UserResponseDto> result = new ArrayList<>();
        for (User user : users) {
            result.add(UserMapper.toDto(user));
        }
        return result;
    }

    public static User toEntity(UserRequestDto dto) {
        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }
}

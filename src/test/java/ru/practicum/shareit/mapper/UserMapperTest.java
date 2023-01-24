package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserMapperTest {
    UserRequestDto userDto;

    @BeforeEach
    void setValue() {
        this.userDto = UserRequestDto.builder().name("name").email("mail@mail.ru").build();
    }

    @Test
    public void toUserTest() {
        User user = UserMapper.toEntity(userDto);
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }
}

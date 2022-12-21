package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserResponseDto {
    @NotBlank(message = "У пользователя должно быть указано имя!")
    private final String name;
    @Email(message = "Неверная маска электронной почты!")
    private final String email;
}

package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
public class UserResponseDto {
    private Long id;
    @NotBlank(message = "У пользователя должно быть указано имя!")
    private final String name;
    @NotNull(message = "У пользователя должна быть указана электронная почта!")
    @Email(message = "Неверная маска электронной почты!")
    private final String email;
}

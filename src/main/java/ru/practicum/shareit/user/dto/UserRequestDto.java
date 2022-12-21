package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.interfaces.OnAdd;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class UserRequestDto {
    @NotBlank(message = "У пользователя должно быть указано имя!", groups = OnAdd.class)
    private final String name;
    @NotNull(message = "У пользователя должна быть указана электронная почта при регистрации!", groups = OnAdd.class)
    @Email(message = "Неверная маска электронной почты!", groups = OnAdd.class)
    private final String email;
}

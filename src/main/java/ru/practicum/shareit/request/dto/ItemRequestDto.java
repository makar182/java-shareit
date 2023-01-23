package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.interfaces.OnAdd;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class ItemRequestDto {
    @NotBlank(message = "У запроса должно быть описание!", groups = OnAdd.class)
    private String description;
}

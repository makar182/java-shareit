package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.interfaces.OnAdd;

@Getter
@Setter
@NoArgsConstructor
public class ItemRequestDto {
    private String description;
}

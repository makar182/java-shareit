package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserRequestDto {
    private String name;
    private String email;

    public UserRequestDto(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public UserRequestDto() {
    }
}

package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Data
@Builder
public class ItemDtoResponse {
    private Long id;
    @NotNull(message = "У вещи должно быть название!")
    @NotBlank(message = "У вещи должно быть название!")
    private String name;
    @NotBlank(message = "У вещи должно быть описание!")
    @Size(max = 1024, message = "Размер описания не должен превышать 255 символов!")
    private String description;
    @NotNull
    private Boolean available;
    @NotNull
    private Long userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemDtoResponse that = (ItemDtoResponse) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

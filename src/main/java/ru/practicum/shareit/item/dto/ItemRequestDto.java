package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.interfaces.OnAdd;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Getter
@Setter
@Builder
public class ItemRequestDto {
    private Long id;
    @NotNull(message = "У вещи должно быть название!", groups = OnAdd.class)
    @NotBlank(message = "У вещи должно быть название!", groups = OnAdd.class)
    private String name;
    @NotBlank(message = "У вещи должно быть описание!", groups = OnAdd.class)
    @Size(max = 1024, message = "Размер описания не должен превышать 255 символов!", groups = OnAdd.class)
    private String description;
    @NotNull(message = "У вещи должна быть указана доступность!", groups = OnAdd.class)
    private Boolean available;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemRequestDto that = (ItemRequestDto) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

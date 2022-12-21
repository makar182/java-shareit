package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.interfaces.OnAdd;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class Item {
    private Long id;
    @NotNull(message = "У вещи должно быть название!", groups = OnAdd.class)
    @NotBlank(message = "У вещи должно быть название!", groups = OnAdd.class)
    private String name;
    @NotBlank(message = "У вещи должно быть описание!", groups = OnAdd.class)
    @Size(max = 1024, message = "Размер описания не должен превышать 255 символов!", groups = OnAdd.class)
    private String description;
    @NotNull(message = "У вещи должна быть указана доступность!", groups = OnAdd.class)
    private Boolean available;
    @NotNull
    private Long userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return id.equals(item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

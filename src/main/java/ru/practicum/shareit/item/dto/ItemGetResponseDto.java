package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Builder
public class ItemGetResponseDto {
    private Long id;
    @NotNull(message = "У вещи должно быть название!")
    @NotBlank(message = "У вещи должно быть название!")
    private String name;
    @NotBlank(message = "У вещи должно быть описание!")
    @Size(max = 1024, message = "Размер описания не должен превышать 255 символов!")
    private String description;
    @NotNull
    private Boolean available;
    private LastAndNextBookings lastBooking;
    private LastAndNextBookings nextBooking;
    private List<Comments> comments;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemGetResponseDto that = (ItemGetResponseDto) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Getter
    @Setter
    public static class LastAndNextBookings {
        private Long id;
        private Long bookerId;

        public LastAndNextBookings(Booking booking) {
            this.id = booking.getId();
            this.bookerId = booking.getBooker().getId();
        }
    }

    @Getter
    @Setter
    public static class Comments {
        private Long id;
        private String text;
        private String authorName;
        private LocalDateTime created;

        public Comments(Comment comment) {
            this.id = comment.getId();
            this.text = comment.getText();
            this.authorName = comment.getAuthor().getName();
            this.created = comment.getCreated();
        }
    }
}

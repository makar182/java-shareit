package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class BookingResponseDto {
    @NotNull
    private Long id;
    @NotNull
    private LocalDateTime start;
    @NotNull
    private LocalDateTime end;
    @NotNull
    private BookingStatus status;
    @NotNull
    private Booker booker;
    @NotNull
    private BookedItem item;

    @Getter
    @Setter
    public static class Booker {
        private Long id;

        public Booker(User user) {
            this.id = user.getId();
        }
    }

    @Getter
    @Setter
    public static class BookedItem {
        private Long id;
        private String name;

        public BookedItem(Item item) {
            this.id = item.getId();
            this.name = item.getName();
        }
    }
}

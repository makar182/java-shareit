package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.dto.ItemForBookingResponseDto;
import ru.practicum.shareit.user.dto.UserInBookingResponseDto;

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
    private UserInBookingResponseDto booker;
    @NotNull
    private ItemForBookingResponseDto item;
}

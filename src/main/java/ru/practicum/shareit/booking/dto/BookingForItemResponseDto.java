package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
public class BookingForItemResponseDto {
    @NotNull
    private Long id;
    @NotNull
    private Long bookerId;
}

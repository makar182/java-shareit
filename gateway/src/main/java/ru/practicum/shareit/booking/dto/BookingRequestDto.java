package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.validator.BookingDateConstraint;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@BookingDateConstraint
public class BookingRequestDto {
    private Long itemId;
    @FutureOrPresent
    private LocalDateTime start;
    @Future
    private LocalDateTime end;


}

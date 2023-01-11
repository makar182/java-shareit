package ru.practicum.shareit.booking.enums;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.UnsupportedBookingStatusException;

@Slf4j
public enum BookingState {
    ALL,
    CURRENT,
    FUTURE,
    PAST,
    REJECTED,
    WAITING;

    public static BookingState getByString(String state) {
        try {
            return BookingState.valueOf(state);
        } catch (RuntimeException e) {
            log.info(String.format("Неподдерживаемый статус %s", state));
            throw new UnsupportedBookingStatusException(String.format("Неподдерживаемый статус %s", state));
        }
    }
}

package ru.practicum.shareit.exception;

public class BookingChangeStatusException extends RuntimeException {
    public BookingChangeStatusException(String message) {
        super(message);
    }
}

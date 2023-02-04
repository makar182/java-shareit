package ru.practicum.shareit.exception;

public class BookingNotExistException extends RuntimeException {
    public BookingNotExistException(String message) {
        super(message);
    }
}

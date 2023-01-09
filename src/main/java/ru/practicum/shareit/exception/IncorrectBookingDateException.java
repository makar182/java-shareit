package ru.practicum.shareit.exception;

public class IncorrectBookingDateException extends RuntimeException {
    public IncorrectBookingDateException(String message) {
        super(message);
    }
}

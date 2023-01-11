package ru.practicum.shareit.exception;

public class UserNotAllowedToGetBookingException extends RuntimeException {
    public UserNotAllowedToGetBookingException(String message) {
        super(message);
    }
}

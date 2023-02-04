package ru.practicum.shareit.exception;

public class BookingItemNotOwnedByUserException extends RuntimeException {
    public BookingItemNotOwnedByUserException(String message) {
        super(message);
    }
}

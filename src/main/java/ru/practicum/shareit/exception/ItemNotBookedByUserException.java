package ru.practicum.shareit.exception;

public class ItemNotBookedByUserException extends RuntimeException{
    public ItemNotBookedByUserException(String message) {
        super(message);
    }
}

package ru.practicum.shareit.exception;

public class ItemRequestNotExistExceptionException extends RuntimeException{
    public ItemRequestNotExistExceptionException(String message) {
        super(message);
    }
}

package ru.practicum.shareit.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.DuplicateUserEmailException;
import ru.practicum.shareit.exception.ItemNotValidPropertiesException;
import ru.practicum.shareit.exception.UserNotExistException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateUserEmailException(final DuplicateUserEmailException e) {
        log.error("Ошибка валидации пользователя:" + e.getMessage());
        return new ErrorResponse("Ошибка валидации пользователя", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserNotExistException(final UserNotExistException e) {
        log.error("Ошибка валидации пользователя:" + e.getMessage());
        return new ErrorResponse("Ошибка валидации пользователя", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleItemNotValidPropertiesException(final ItemNotValidPropertiesException e) {
        log.error("Ошибка валидации предмета:" + e.getMessage());
        return new ErrorResponse("Ошибка валидации предмета", e.getMessage());
    }
}


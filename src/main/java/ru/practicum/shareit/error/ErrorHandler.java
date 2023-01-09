package ru.practicum.shareit.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.*;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateUserEmailException(final DuplicateUserEmailException e) {
        log.error("Ошибка валидации пользователя: " + e.getMessage());
        return new ErrorResponse("Ошибка валидации пользователя", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotExistException(final UserNotExistException e) {
        log.error("Ошибка валидации пользователя: " + e.getMessage());
        return new ErrorResponse("Ошибка валидации пользователя", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleItemNotExistException(final ItemNotExistException e) {
        log.error("Ошибка валидации предмета: " + e.getMessage());
        return new ErrorResponse("Ошибка валидации предмета", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleBookingNotExistException(final BookingNotExistException e) {
        log.error("Ошибка валидации бронирования: " + e.getMessage());
        return new ErrorResponse("Ошибка валидации бронирования", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleBookingItemNotOwnedByUserException(final BookingItemNotOwnedByUserException e) {
        log.error("Ошибка свойств бронирования: " + e.getMessage());
        return new ErrorResponse("Ошибка свойств бронирования", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectBookingDateException(final IncorrectBookingDateException e) {
        log.error("Ошибка дат бронирования: " + e.getMessage());
        return new ErrorResponse("Ошибка дат бронирования", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleIncorrectBookerException(final IncorrectBookerException e) {
        log.error("Ошибка бронирования: " + e.getMessage());
        return new ErrorResponse("Ошибка бронирования", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleItemNotBookedByUserException(final ItemNotBookedByUserException e) {
        log.error("Ошибка: " + e.getMessage());
        return new ErrorResponse("Ошибка", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBookingChangeStatusException(final BookingChangeStatusException e) {
        log.error("Ошибка смены статуса: " + e.getMessage());
        return new ErrorResponse("Ошибка смены статуса", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotAllowedToGetBookingException(final UserNotAllowedToGetBookingException e) {
        log.error("Ошибка доступа: " + e.getMessage());
        return new ErrorResponse("Ошибка доступа", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleItemNotValidPropertiesException(final ItemNotValidPropertiesException e) {
        log.error("Ошибка валидации предмета: " + e.getMessage());
        return new ErrorResponse("Ошибка валидации предмета", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleItemNotAvailableException(final ItemNotAvailableException e) {
        log.error("Ошибка валидации предмета: " + e.getMessage());
        return new ErrorResponse("Ошибка валидации предмета", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
        log.error("Ошибка валидации: " + e.getMessage());
        return new ErrorResponse("Ошибка валидации", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnsupportedBookingStatusException(final UnsupportedBookingStatusException e) {
        log.error(e.getMessage());
        return new ErrorResponse("Unknown state: UNSUPPORTED_STATUS", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.error("Неизвестная ошибка: " + e.getMessage());
        return new ErrorResponse("Неизвестная ошибка:", e.getMessage());
    }
}

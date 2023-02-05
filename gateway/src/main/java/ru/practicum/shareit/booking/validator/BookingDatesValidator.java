package ru.practicum.shareit.booking.validator;

import ru.practicum.shareit.booking.dto.BookingRequestDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BookingDatesValidator implements ConstraintValidator<BookingDateConstraint, BookingRequestDto> {
    @Override
    public void initialize(BookingDateConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(BookingRequestDto bookingRequestDto, ConstraintValidatorContext constraintValidatorContext) {
        return bookingRequestDto.getStart().isBefore(bookingRequestDto.getEnd());
    }
}

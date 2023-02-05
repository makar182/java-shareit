package ru.practicum.shareit.booking.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = BookingDatesValidator.class)
@Target( { ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface BookingDateConstraint {
    String message() default "Дата начала бронирования не может быть позже даты окончания!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

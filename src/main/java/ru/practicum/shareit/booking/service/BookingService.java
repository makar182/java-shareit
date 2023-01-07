package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking getBookingById(Long userId, Long bookingId);

    List<Booking> getBookingsByUser(Long userId, BookingState state);

    List<Booking> getBookingsByOwner(Long userId, BookingState state);

    Booking addBooking(Booking booking, Long userId);

    Booking approveBooking(Long userId, Long bookingId, Boolean approved);
}

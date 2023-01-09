package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking getBookingById(Long userId, Long bookingId);

    List<Booking> getBookingsByBooker(Long userId, String state);

    List<Booking> getBookingsByOwner(Long userId, String state);

    Booking addBooking(Booking booking, Long userId);

    Booking approveBooking(Long userId, Long bookingId, Boolean approved);
}

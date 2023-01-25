package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking getBookingById(Long userId, Long bookingId);

    List<Booking> getBookingsByBooker(Long userId, String state, int from, int size);

    List<Booking> getBookingsByOwner(Long userId, String state, int from, int size);

    Booking addBooking(Booking booking, Long userId);

    Booking approveBooking(Long userId, Long bookingId, Boolean approved);
}

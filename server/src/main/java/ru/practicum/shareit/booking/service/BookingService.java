package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingRequestDto;
import ru.practicum.shareit.booking.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto getBookingById(Long userId, Long bookingId);

    List<BookingResponseDto> getBookingsByBooker(Long userId, String state, int from, int size);

    List<BookingResponseDto> getBookingsByOwner(Long userId, String state, int from, int size);

    BookingResponseDto addBooking(BookingRequestDto bookingRequestDto, Long userId);

    BookingResponseDto approveBooking(Long userId, Long bookingId, Boolean approved);
}

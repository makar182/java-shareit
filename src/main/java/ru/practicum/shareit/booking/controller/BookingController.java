package ru.practicum.shareit.booking.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable("bookingId") Long bookingId) {
        return BookingMapper.toDto(bookingService.getBookingById(userId, bookingId));
    }

    @GetMapping
    public List<BookingResponseDto> getBookingsByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
                                                      @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                                      @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        return BookingMapper.toDtoList(bookingService.getBookingsByBooker(userId, state, from, size));
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getBookingsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
                                                       @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                                       @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        return BookingMapper.toDtoList(bookingService.getBookingsByOwner(userId, state, from, size));
    }

    @PostMapping
    public BookingResponseDto addBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Valid @RequestBody BookingRequestDto booking) {
        return BookingMapper.toDto(bookingService.addBooking(BookingMapper.toEntity(booking), userId));
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable("bookingId") Long bookingId,
                                             @RequestParam("approved") Boolean approved) {
        return BookingMapper.toDto(bookingService.approveBooking(userId, bookingId, approved));
    }
}

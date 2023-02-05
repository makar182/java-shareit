package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable("bookingId") Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
                                                    @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                                    @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        log.info("Get booking by user with state {}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingClient.getBookingsByBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
                                                       @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                                       @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        log.info("Get booking by owner with state {}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingClient.getBookingsByOwner(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Valid @RequestBody BookingRequestDto booking) {
        log.info("Add booking by user {} with booking {}", userId, booking);
        return bookingClient.addBooking(userId, booking);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable("bookingId") Long bookingId,
                                             @RequestParam("approved") String approved) {
        log.info("Approve booking by user {} with bookingId {} and approved {}", userId, bookingId, approved);
        return bookingClient.approveBooking(userId, bookingId, approved);
    }
}

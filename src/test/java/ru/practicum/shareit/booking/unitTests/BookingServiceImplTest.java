package ru.practicum.shareit.booking.unitTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

import javax.transaction.Transactional;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(properties = {"db.name=test"})
@SpringJUnitConfig(BookingServiceImpl.class)

public class BookingServiceImplTest {
    private final BookingServiceImpl bookingService;

    @BeforeAll
    void setUpBeforeAll() {
    }

    @Test
    void getBookingById() {
        Booking result = bookingService.getBookingById(1L, 1L);
    }
}

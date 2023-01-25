package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingMapperTest {
    private Booking booking;
    private Item item;
    private User user;

    @BeforeEach
    void setValues() {
        this.item = new Item();
        item.setRequest(new ItemRequest());
        item.setId(1L);
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);

        this.user = new User();
        user.setId(1L);
        user.setEmail("mail@mail.ru");
        user.setName("name");

        this.booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(5));
        booking.setBooker(user);
    }

    @Test
    public void bookingMapperToBookingDtoTest() {
        BookingResponseDto bookingDtoResult = BookingMapper.toDto(booking);

        assertEquals(bookingDtoResult.getId(), booking.getId(), "Неверно присвоен ID");
        assertEquals(bookingDtoResult.getBooker().getId(), booking.getBooker().getId(), "Неверно присвоен ID букера");
        assertEquals(bookingDtoResult.getItem().getId(), booking.getItem().getId(), "Неверно присвоен ID для вещи");
        assertEquals(bookingDtoResult.getStatus(), booking.getStatus(), "Неверно присвоен статус");
        assertEquals(bookingDtoResult.getStart(), booking.getStart(), "Неверно присвоено время начала аренды");
        assertEquals(bookingDtoResult.getEnd(), booking.getEnd(), "Неверно присвоено время окончания аренды");
    }

    @Test
    public void bookingMapper() {
        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        Booking bookingResult = BookingMapper.toEntity(bookingRequestDto);

        assertEquals(bookingResult.getItem().getId(), bookingRequestDto.getItemId(),
                "Неверно присвоен ID для вещи");
        assertEquals(bookingResult.getStart(), bookingRequestDto.getStart(),
                "Неверно присвоено время начала аренды");
        assertEquals(bookingResult.getEnd(), bookingRequestDto.getEnd(),
                "Неверно присвоено время окончания аренды");
    }
}


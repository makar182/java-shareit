package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingForItemResponseDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.ArrayList;
import java.util.List;

public class BookingMapper {
    public static Booking toEntity(BookingRequestDto bookingRequestDto) {
        return new Booking(null, bookingRequestDto.getStart(), bookingRequestDto.getEnd(),
                Item.builder().id(bookingRequestDto.getItemId()).build(), null, BookingStatus.WAITING);
    }

    public static BookingResponseDto toDto(Booking booking) {
        return BookingResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toBookingResponseDto(booking.getItem()))
                .booker(UserMapper.toBookingResponseDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public static BookingForItemResponseDto toBookingForItemResponseDto(Booking booking) {
        if (booking == null) {
            return null;
        } else {
            return BookingForItemResponseDto.builder()
                    .id(booking.getId())
                    .bookerId(booking.getBooker().getId())
                    .build();
        }
    }

    public static List<BookingResponseDto> toDtoList(List<Booking> bookings) {
        List<BookingResponseDto> result = new ArrayList<>();
        for (Booking booking : bookings) {
            result.add(BookingMapper.toDto(booking));
        }
        return result;
    }
}

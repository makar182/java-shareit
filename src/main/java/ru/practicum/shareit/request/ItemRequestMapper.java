package ru.practicum.shareit.request;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.List;

public class ItemRequestMapper {
    public static ItemRequest toEntity(ItemRequestDto itemRequestDto) {
        return new ItemRequest(null, itemRequestDto.getDescription(), null);
    }

    public static ItemResponseDto toDto(ItemRequest itemRequest) {
        return null;
    }

    public static List<ItemResponseDto> toDtoList(List<ItemRequest> itemRequests) {
//        List<BookingResponseDto> result = new ArrayList<>();
//        for (Booking booking : bookings) {
//            result.add(BookingMapper.toDto(booking));
//        }
        return null;
    }
}

package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemGetResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemForBookingResponseDto;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ItemMapper {
    private final BookingRepository bookingRepository;

    public ItemMapper(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public ItemResponseDto toItemResponseDto(Item item) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public ItemGetResponseDto toItemGetResponseDto(Item item) {
        List<Booking> bookings = bookingRepository.findAllByItem_Id(item.getId());

        Booking lastBooking = bookings.stream()
                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(Booking::getStart))
                .orElse(null);

        Booking nextBooking = bookings.stream()
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(Booking::getStart))
                .orElse(null);

        assert lastBooking != null;
        assert nextBooking != null;
        return ItemGetResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(BookingMapper.toBookingForItemResponseDto(lastBooking))
                .nextBooking(BookingMapper.toBookingForItemResponseDto(nextBooking))
                .build();
    }

    public List<ItemGetResponseDto> toItemGetResponseDtoList(List<Item> items) {
        List<ItemGetResponseDto> result = new ArrayList<>();
        for (Item item : items) {
            result.add(toItemGetResponseDto(item));
        }
        return result;
    }

    public static ItemForBookingResponseDto toBookingResponseDto(Item item) {
        return ItemForBookingResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .build();
    }

    public static Item toEntity(ItemRequestDto itemRequestDto) {
        return new Item(null, itemRequestDto.getName(), itemRequestDto.getDescription(), itemRequestDto.getAvailable(), null);
    }
}

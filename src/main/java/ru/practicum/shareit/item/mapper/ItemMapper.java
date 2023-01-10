package ru.practicum.shareit.item.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemMapper {
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public ItemMapper(BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    public ItemMainResponseDto toItemMainResponseDto(Item item) {
        return ItemMainResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public ItemGetResponseDto toItemGetResponseDto(Item item, Long ownerId) {
        List<Booking> bookings = bookingRepository.findAllByItem_Id(item.getId());
        List<Comment> comments = commentRepository.findAllByItem_Id(item.getId());
        Booking lastBooking = null;
        Booking nextBooking = null;

        if (item.getOwner().getId().equals(ownerId)) {
            lastBooking = bookings.stream()
                    .filter(booking -> booking.getStatus().equals(BookingStatus.APPROVED))
                    .filter(booking -> !booking.getEnd().isAfter(LocalDateTime.now())
                            || !booking.getStart().isAfter(LocalDateTime.now()))
                    .max(Comparator.comparing(Booking::getEnd))
                    .orElse(null);

            nextBooking = bookings.stream()
                    .filter(booking -> booking.getStatus().equals(BookingStatus.APPROVED))
                    .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                    .min(Comparator.comparing(Booking::getStart))
                    .orElse(null);
        }

        assert comments != null;
        return ItemGetResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBooking == null ? null : new ItemGetResponseDto.LastAndNextBookings(lastBooking))
                .nextBooking(nextBooking == null ? null : new ItemGetResponseDto.LastAndNextBookings(nextBooking))
                .comments(getComments(comments))
                .build();
    }

    public List<ItemGetResponseDto> toItemGetResponseDtoList(List<Item> items, Long ownerId) {
        List<ItemGetResponseDto> result = new ArrayList<>();
        for (Item item : items) {
            result.add(toItemGetResponseDto(item, ownerId));
        }
        return result.stream()
                .sorted((o1, o2) -> {
                    if (o1.getLastBooking() == null && o1.getNextBooking() == null) {
                        return 1;
                    } else if (o2.getLastBooking() == null && o2.getNextBooking() == null) {
                        return -1;
                    } else {
                        return 0;
                    }
                }).collect(Collectors.toList());
    }

    public static Item toItemEntity(ItemRequestDto itemRequestDto) {
        return new Item(null, itemRequestDto.getName(), itemRequestDto.getDescription(), itemRequestDto.getAvailable(), null, null);
    }

    public static CommentResponseDto toCommentResponseDto(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public static Comment toCommentEntity(CommentRequestDto commentRequestDto) {
        return Comment.builder()
                .text(commentRequestDto.getText())
                .build();
    }

    private List<ItemGetResponseDto.Comments> getComments(List<Comment> comments) {
        List<ItemGetResponseDto.Comments> result = new ArrayList<>();
        for (Comment comment : comments) {
            result.add(new ItemGetResponseDto.Comments(comment));
        }
        return result;
    }
}

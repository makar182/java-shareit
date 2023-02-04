package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemNotBookedByUserException;
import ru.practicum.shareit.exception.ItemNotExistException;
import ru.practicum.shareit.exception.ItemNotValidPropertiesException;
import ru.practicum.shareit.exception.UserNotExistException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemMainResponseDto addItem(Long userId, ItemRequestDto itemRequestDto) {
        User user = getUserIfExists(userId);
        Item item = itemMapper.toItemEntity(itemRequestDto);
        item.setOwner(user);
        Item newItem = itemRepository.saveAndFlush(item);

        log.info(String.format("Предмет %s успешно добавлен", newItem));
        return ItemMapper.toItemMainResponseDto(newItem);
    }

    @Override
    public ItemMainResponseDto updateItem(Long userId, Long itemId, ItemRequestDto itemRequestDto) {
        Item item = itemMapper.toItemEntity(itemRequestDto);

        Item oldItem = itemRepository.findById(itemId).orElseThrow(() -> {
            log.info(String.format("Предмет %d: %s не существует", itemId, item));
            throw new ItemNotExistException(String.format("Предмет %d: %s не существует", itemId, item));
        });

        if (!oldItem.getOwner().getId().equals(userId)) {
            log.info(String.format("Изменяемый предмет не принадлежит пользователю %d", userId));
            throw new ItemNotValidPropertiesException(String.format("Изменяемый предмет не принадлежит пользователю %d", userId));
        }

        String newItemName;
        if (item.getName() != null && !item.getName().isBlank()) {
            newItemName = item.getName();
        } else {
            newItemName = oldItem.getName();
        }

        String newItemDescription;
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            newItemDescription = item.getDescription();
        } else {
            newItemDescription = oldItem.getDescription();
        }

        Boolean newItemAvailableFlag;
        if (item.getAvailable() != null) {
            newItemAvailableFlag = item.getAvailable();
        } else {
            newItemAvailableFlag = oldItem.getAvailable();
        }

        ItemRequest newItemRequest = item.getRequest() != null ? item.getRequest() : oldItem.getRequest();

        Item itemToAdd = new Item(oldItem.getId(), newItemName, newItemDescription, newItemAvailableFlag, oldItem.getOwner(), oldItem.getComments(), newItemRequest);
        Item newItem = itemRepository.saveAndFlush(itemToAdd);
        log.info(String.format("Предмет %s успешно обновлен", newItem));
        return ItemMapper.toItemMainResponseDto(newItem);
    }

    @Override
    public ItemGetResponseDto getItemById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            log.info(String.format("Предмет %d не найден", itemId));
            throw new ItemNotExistException(String.format("Предмет %d не найден", itemId));
        });
        ItemGetResponseDto result = itemMapper.toItemGetResponseDto(item, userId);
        log.info(String.format("Предмет %s выгружен", result));
        return result;
    }

    @Override
    public List<ItemGetResponseDto> getItemsByUserId(Long userId, Integer from, Integer size) {
        checkFromSizeArguments(from, size);

        getUserIfExists(userId);

        int page = from == 0 ? 0 : (from / size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("created").ascending());
        List<ItemGetResponseDto> result = itemMapper.toItemGetResponseDtoList(itemRepository.findAllByOwnerId(userId, pageable), userId);
        log.info(String.format("Выгружен список предметов по пользователю %d", userId));
        return result;
    }

    @Override
    public List<ItemGetResponseDto> getItemsByDescription(Long userId, String itemDescription, Integer from, Integer size) {
        checkFromSizeArguments(from, size);

        if (itemDescription.isBlank()) {
            log.info("Пустая строка поиска /search");
            return List.of();
        } else {
            int page = from == 0 ? 0 : (from / size);
            Pageable pageable = PageRequest.of(page, size, Sort.by("created").ascending());
            List<Item> items = itemRepository.findAllByDescriptionContainingIgnoreCaseAndAvailable(itemDescription.trim(), true, pageable);
            List<ItemGetResponseDto> result = itemMapper.toItemGetResponseDtoList(items, userId);
            log.info(String.format("Выгружен список предметов по описанию '%s'", itemDescription));
            return result;
        }
    }

    @Override
    public CommentResponseDto addComment(Long userId, Long itemId, CommentRequestDto commentRequestDto) {
        User user = getUserIfExists(userId);

        Comment comment = ItemMapper.toCommentEntity(commentRequestDto);

        Booking bookingExist = bookingRepository.findAllByItem_Id(itemId).stream()
                .filter(booking -> booking.getBooker().getId().equals(userId))
                .filter(booking -> booking.getStatus().equals(BookingStatus.APPROVED))
                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                .findFirst()
                .orElseThrow(() -> {
                    log.info(String.format("Пользователь %d не брал предмет №%d в аренду!", userId, itemId));
                    throw new ItemNotBookedByUserException(String.format("Пользователь %d не брал предмет №%d в аренду!", userId, itemId));
                });
        comment.setItem(bookingExist.getItem());
        comment.setAuthor(user);
        return ItemMapper.toCommentResponseDto(commentRepository.saveAndFlush(comment));
    }

    private User getUserIfExists(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.info(String.format("Пользователя №%d не существует!", userId));
            throw new UserNotExistException(String.format("Пользователя №%d не существует!", userId));
        });
    }

    private void checkFromSizeArguments(int from, int size) {
        if (from < 0 || size <= 0) {
            log.info("Отрицательные значения параметров from и size недопустимы!");
            throw new IllegalArgumentException("Отрицательные значения параметров from и size недопустимы!");
        }
    }
}

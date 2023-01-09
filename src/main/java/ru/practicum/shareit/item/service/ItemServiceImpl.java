package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.ItemGetResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    ItemMapper itemMapper;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        itemMapper = new ItemMapper(this.bookingRepository, commentRepository);
    }

    @Override
    public Item addItem(Long userId, Item item) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.info(String.format("Пользователь %d не существует", userId));
            throw new UserNotExistException(String.format("Пользователь %d не существует", userId));
        });

        item.setOwner(user);
        Item newItem = itemRepository.saveAndFlush(item);

        log.info(String.format("Предмет %s успешно добавлен", newItem));
        return newItem;
    }

    @Override
    public Item updateItem(Long userId, Long itemId, Item item) {
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

        Item itemToAdd = new Item(oldItem.getId(), newItemName, newItemDescription, newItemAvailableFlag, oldItem.getOwner(), oldItem.getComments());
        Item newItem = itemRepository.saveAndFlush(itemToAdd);
        log.info(String.format("Предмет %s успешно обновлен", newItem));
        return newItem;
    }

    @Override
    public ItemGetResponseDto getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            log.info(String.format("Предмет %d не найден", itemId));
            throw new ItemNotExistException(String.format("Предмет %d не найден", itemId));
        });
        ItemGetResponseDto result = itemMapper.toItemGetResponseDto(item, null);
        log.info(String.format("Предмет %s выгружен", result));
        return result;
    }

    @Override
    public List<ItemGetResponseDto> getItemsByUserId(Long userId) {
        List<ItemGetResponseDto> result = itemMapper.toItemGetResponseDtoList(itemRepository.findAllByOwnerId(userId), userId);
        log.info(String.format("Выгружен список предметов по пользователю %d", userId));
        return result;
    }

    @Override
    public List<ItemGetResponseDto> getItemsByDescription(String itemDescription) {
        if (itemDescription.isBlank()) {
            log.info("Пустая строка поиска /search");
            return List.of();
        } else {
            List<Item> items = itemRepository.findAllByDescriptionContainingIgnoreCaseAndAvailable(itemDescription.trim(), true);
            List<ItemGetResponseDto> result = itemMapper.toItemGetResponseDtoList(items);
            log.info(String.format("Выгружен список предметов по описанию '%s'", itemDescription));
            return result;
        }
    }

    @Override
    public Comment addComment(Long userId, Long itemId, Comment comment) {
        User user = checkUserExists(userId);

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
        return commentRepository.saveAndFlush(comment);
    }

    private User checkUserExists(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.info(String.format("Пользователя №%d не существует!", userId));
            throw new UserNotExistException(String.format("Пользователя №%d не существует!", userId));
        });
    }
}

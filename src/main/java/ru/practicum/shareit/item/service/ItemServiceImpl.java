package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotExistException;
import ru.practicum.shareit.exception.ItemNotValidPropertiesException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Item addItem(Long userId, Item item) {
        if (userRepository.getUserById(userId) == null) {
            log.info(String.format("Пользователь %d не существует", userId));
            throw new ItemNotValidPropertiesException(String.format("Пользователь %d не существует", userId));
        }

        item.setUserId(userId);
        Item newItem = itemRepository.addItem(item);
        log.info(String.format("Предмет %s успешно добавлен", newItem));
        return newItem;
    }

    @Override
    public Item updateItem(Long userId, Long itemId, Item item) {
        Item oldItem = getItemById(itemId);
        if (oldItem == null) {
            log.info(String.format("Изменяемый предмет %d не существует", itemId));
            throw new ItemNotExistException(String.format("Изменяемый предмет %d не существует", itemId));
        } else if (!oldItem.getUserId().equals(userId)) {
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

        Item itemToAdd = Item.builder()
                .id(oldItem.getId())
                .name(newItemName)
                .description(newItemDescription)
                .available(newItemAvailableFlag)
                .userId(oldItem.getUserId())
                .build();

        Item newItem = itemRepository.updateItem(itemToAdd);
        log.info(String.format("Предмет %s успешно обновлен", newItem));
        return newItem;
    }

    @Override
    public Item getItemById(Long itemId) {
        Item item = itemRepository.getItemById(itemId);
        log.info(String.format("Предмет %s выгружен", item));
        return item;
    }

    @Override
    public List<Item> getItemsByUserId(Long userId) {
        List<Item> items = itemRepository.getItemsByUserId(userId);
        log.info(String.format("Выгружен список предметов по пользователю %d", userId));
        return items;
    }

    @Override
    public List<Item> getItemsByDescription(String itemDescription) {
        if(itemDescription.isBlank()) {
            log.info("Пустая строка поиска /search");
            return List.of();
        } else {
            List<Item> items = itemRepository.getItemsByDescription(itemDescription.trim());
            log.info(String.format("Выгружен список предметов по описанию '%s'", itemDescription));
            return items;
        }
    }
}

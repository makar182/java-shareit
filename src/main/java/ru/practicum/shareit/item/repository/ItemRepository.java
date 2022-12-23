package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Optional<Item> addItem(Item item);

    Optional<Item> updateItem(Item item);

    Optional<Item> getItemById(long itemId);

    List<Item> getItemsByUserId(long userId);

    List<Item> getItemsByDescription(String itemDescription);
}

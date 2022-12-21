package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item addItem(Item item);

    Item updateItem(Item item);

    Item getItemById(long itemId);

    List<Item> getItemsByUserId(long userId);

    List<Item> getItemsByDescription(String itemDescription);
}

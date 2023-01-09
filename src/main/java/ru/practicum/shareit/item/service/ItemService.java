package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemGetResponseDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item addItem(Long userId, Item item);

    Item updateItem(Long userId, Long itemId, Item item);

    ItemGetResponseDto getItemById(Long itemId);

    List<ItemGetResponseDto> getItemsByUserId(Long userId);

    List<ItemGetResponseDto> getItemsByDescription(String itemDescription);
}

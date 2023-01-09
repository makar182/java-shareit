package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemGetResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item addItem(Long userId, Item item);

    Item updateItem(Long userId, Long itemId, Item item);

    ItemGetResponseDto getItemById(Long userId, Long itemId);

    List<ItemGetResponseDto> getItemsByUserId(Long userId);

    List<ItemGetResponseDto> getItemsByDescription(Long userId, String itemDescription);

    Comment addComment(Long userId, Long itemId, Comment comment);
}

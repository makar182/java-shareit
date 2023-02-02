package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemMainResponseDto addItem(Long userId, ItemRequestDto item);

    ItemMainResponseDto updateItem(Long userId, Long itemId, ItemRequestDto item);

    ItemGetResponseDto getItemById(Long userId, Long itemId);

    List<ItemGetResponseDto> getItemsByUserId(Long userId, Integer from, Integer size);

    List<ItemGetResponseDto> getItemsByDescription(Long userId, String itemDescription, Integer from, Integer size);

    CommentResponseDto addComment(Long userId, Long itemId, CommentRequestDto comment);
}

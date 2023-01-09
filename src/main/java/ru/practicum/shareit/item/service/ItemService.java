package ru.practicum.shareit.item.service;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemGetResponseDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item addItem(Long userId, Item item);

    Item updateItem(Long userId, Long itemId, Item item);

    ItemGetResponseDto getItemById(Long itemId);

    List<ItemGetResponseDto> getItemsByUserId(Long userId);

    List<ItemGetResponseDto> getItemsByDescription(String itemDescription);

    Comment addComment(Long userId, Long itemId, Comment comment);
}

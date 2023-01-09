package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.interfaces.OnAdd;
import ru.practicum.shareit.interfaces.OnUpdate;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @Autowired
    public ItemController(ItemService itemService, ItemMapper itemMapper) {
        this.itemService = itemService;
        this.itemMapper = itemMapper;
    }

    @PostMapping
    public ItemResponseDto addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @Validated(OnAdd.class) @RequestBody ItemRequestDto itemRequestDto) {
        return itemMapper.toItemResponseDto(itemService.addItem(userId, ItemMapper.toItemEntity(itemRequestDto)));
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable("itemId") Long itemId,
                                      @Validated(OnUpdate.class) @RequestBody ItemRequestDto itemRequestDto) {
        return itemMapper.toItemResponseDto(itemService.updateItem(userId, itemId, ItemMapper.toItemEntity(itemRequestDto)));
    }

    @GetMapping("/{itemId}")
    public ItemGetResponseDto getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable("itemId") long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemGetResponseDto> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemsByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemGetResponseDto> getItemsByDescription(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                          @RequestParam("text") String itemDescription) {
        return itemService.getItemsByDescription(userId, itemDescription);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable("itemId") Long itemId,
                                         @Validated @RequestBody CommentRequestDto commentRequestDto) {
        return ItemMapper.toCommentResponseDto(itemService.addComment(userId, itemId, ItemMapper.toCommentEntity(commentRequestDto)));
    }

}

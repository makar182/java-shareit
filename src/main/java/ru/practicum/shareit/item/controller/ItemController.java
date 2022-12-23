package ru.practicum.shareit.item.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.interfaces.OnAdd;
import ru.practicum.shareit.interfaces.OnUpdate;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDtoResponse addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @Validated(OnAdd.class) @RequestBody ItemDtoRequest itemDtoRequest) {
        return ItemMapper.toDto(itemService.addItem(userId, ItemMapper.toEntity(itemDtoRequest)));
    }

    @PatchMapping("/{itemId}")
    public ItemDtoResponse updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable("itemId") Long itemId,
                                      @Validated(OnUpdate.class) @RequestBody ItemDtoRequest itemDtoRequest) {
        return ItemMapper.toDto(itemService.updateItem(userId, itemId, ItemMapper.toEntity(itemDtoRequest)));
    }

    @GetMapping("/{itemId}")
    public ItemDtoResponse getItemById(@PathVariable("itemId") long itemId) {
        return ItemMapper.toDto(itemService.getItemById(itemId));
    }

    @GetMapping
    public List<ItemDtoResponse> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return ItemMapper.toDtoList(itemService.getItemsByUserId(userId));
    }

    @GetMapping("/search")
    public List<ItemDtoResponse> getItemsByDescription(@RequestParam("text") String itemDescription) {
        return ItemMapper.toDtoList(itemService.getItemsByDescription(itemDescription));
    }
}

package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.interfaces.OnAdd;
import ru.practicum.shareit.interfaces.OnUpdate;
import ru.practicum.shareit.item.dto.ItemGetResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @Autowired
    public ItemController(ItemService itemService, BookingRepository bookingRepository) {
        this.itemService = itemService;
        this.itemMapper = new ItemMapper(bookingRepository);
    }

    @PostMapping
    public ItemResponseDto addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @Validated(OnAdd.class) @RequestBody ItemRequestDto itemRequestDto) {
        return itemMapper.toItemResponseDto(itemService.addItem(userId, ItemMapper.toEntity(itemRequestDto)));
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable("itemId") Long itemId,
                                      @Validated(OnUpdate.class) @RequestBody ItemRequestDto itemRequestDto) {
        return itemMapper.toItemResponseDto(itemService.updateItem(userId, itemId, ItemMapper.toEntity(itemRequestDto)));
    }

    @GetMapping("/{itemId}")
    public ItemGetResponseDto getItemById(@PathVariable("itemId") long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemGetResponseDto> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemsByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemGetResponseDto> getItemsByDescription(@RequestParam("text") String itemDescription) {
        return itemService.getItemsByDescription(itemDescription);
    }
}

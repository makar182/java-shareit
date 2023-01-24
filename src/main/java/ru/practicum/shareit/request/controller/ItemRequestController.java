package ru.practicum.shareit.request.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.interfaces.OnAdd;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemResponseDto addItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @Validated(OnAdd.class)  @RequestBody ItemRequestDto itemRequestDto) {
        return ItemRequestMapper.toDto(itemRequestService.addItemRequest(ItemRequestMapper.toEntity(itemRequestDto), userId));
    }

    @GetMapping("/{requestId}")
    public ItemResponseDto getItemRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable("requestId") Long requestId) {
        return ItemRequestMapper.toDto(itemRequestService.getItemRequestById(requestId, userId));
    }

    @GetMapping
    public List<ItemResponseDto> getItemRequestsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return ItemRequestMapper.toDtoList(itemRequestService.getItemRequestsByOwner(userId));
    }

    @GetMapping("/all")
    public List<ItemResponseDto> getItemRequestsByVisitor(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                          @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                                          @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        return ItemRequestMapper.toDtoList(itemRequestService.getItemRequestsByVisitor(userId, from, size));
    }
}

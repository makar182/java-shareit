package ru.practicum.shareit.request.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
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
                                          @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return ItemRequestMapper.toDto(itemRequestService.addItemRequest(itemRequestDto, userId));
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
                                                          @RequestParam("from") int from,
                                                          @RequestParam("size") int size) {
        return ItemRequestMapper.toDtoList(itemRequestService.getItemRequestsByVisitor(userId, from, size));
    }
}

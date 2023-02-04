package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemResponseDto addItemRequest(ItemRequestDto item, Long userId);

    ItemResponseDto getItemRequestById(Long itemRequestId, Long userId);

    List<ItemResponseDto> getItemRequestsByOwner(Long userId);

    List<ItemResponseDto> getItemRequestsByVisitor(Long userId, Integer from, Integer size);
}

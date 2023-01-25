package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequest addItemRequest(ItemRequest item, Long userId);

    ItemRequest getItemRequestById(Long itemRequestId, Long userId);

    List<ItemRequest> getItemRequestsByOwner(Long userId);

    List<ItemRequest> getItemRequestsByVisitor(Long userId, Integer from, Integer size);
}

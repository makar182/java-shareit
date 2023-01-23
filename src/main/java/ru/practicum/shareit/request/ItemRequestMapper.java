package ru.practicum.shareit.request;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.List;

public class ItemRequestMapper {
    public static ItemRequest toEntity(ItemRequestDto itemRequestDto) {
        return new ItemRequest(itemRequestDto.getDescription(), null, null);
    }

    public static ItemResponseDto toDto(ItemRequest itemRequest) {
        List<ItemResponseDto.NestedItem> nestedItems = new ArrayList<>();
        if (itemRequest.getItems() != null) {
            for (Item item : itemRequest.getItems()) {
                nestedItems.add(new ItemResponseDto.NestedItem(item));
            }
        }

        return ItemResponseDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(nestedItems)
                .build();
    }

    public static List<ItemResponseDto> toDtoList(List<ItemRequest> itemRequests) {
        List<ItemResponseDto> result = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            result.add(ItemRequestMapper.toDto(itemRequest));
        }
        return result;
    }
}

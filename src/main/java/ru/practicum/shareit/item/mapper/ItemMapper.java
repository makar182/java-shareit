package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

    public static ItemDtoResponse toDto(Item item) {
        return ItemDtoResponse.builder()
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static Item toEntity(ItemDtoRequest itemDtoRequest) {
        return Item.builder()
                .name(itemDtoRequest.getName())
                .description(itemDtoRequest.getDescription())
                .available(itemDtoRequest.getAvailable())
                .build();
    }
}

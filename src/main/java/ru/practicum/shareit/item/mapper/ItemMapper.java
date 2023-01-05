package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemMapper {

    public static ItemDtoResponse toDto(Item item) {
        return ItemDtoResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static List<ItemDtoResponse> toDtoList(List<Item> items) {
        List<ItemDtoResponse> result = new ArrayList<>();
        for (Item item : items) {
            result.add(ItemMapper.toDto(item));
        }
        return result;
    }

    public static Item toEntity(ItemDtoRequest itemDtoRequest) {
        return new Item(null, itemDtoRequest.getName(), itemDtoRequest.getDescription(), itemDtoRequest.getAvailable(), null);
    }
}

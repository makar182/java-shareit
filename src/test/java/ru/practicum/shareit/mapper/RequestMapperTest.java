package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequestMapperTest {

    public Item item;
    public User user;
    public ItemRequest request;

    @BeforeEach
    void setValues() {
        this.item = new Item();
        item.setRequest(new ItemRequest());
        item.setId(1L);
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);

        this.user = new User();
        user.setId(1L);
        user.setEmail("mail@mail.ru");
        user.setName("name");

        this.request = new ItemRequest();
        request.setId(1L);
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());
        request.setDescription("Desc");
    }

    @Test
    public void toRequestDtoTest() {
        ItemResponseDto requestDto = ItemRequestMapper.toDto(request);
        assertEquals(requestDto.getId(), request.getId());
        assertEquals(requestDto.getCreated().truncatedTo(ChronoUnit.MINUTES),
                request.getCreated().truncatedTo(ChronoUnit.MINUTES));
        assertEquals(requestDto.getDescription(), request.getDescription());
    }

    @Test
    public void toItemRequestDtoTest() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("desc");

        ItemRequest request =  ItemRequestMapper.toEntity(itemRequestDto);
        assertEquals(request.getDescription(), itemRequestDto.getDescription());
    }
}

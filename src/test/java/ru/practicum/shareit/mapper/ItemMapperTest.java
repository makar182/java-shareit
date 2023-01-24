package ru.practicum.shareit.mapper;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemMainResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringJUnitConfig({BookingRepository.class, CommentRepository.class, ItemRequestRepository.class})
public class ItemMapperTest {
    public Item item;
    public User user;

    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemMapper itemMapper;



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
    }

    @Test
    public void toItemDtoTest() {
        ItemMainResponseDto itemDto = ItemMapper.toItemMainResponseDto(item);
        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getAvailable(), item.getAvailable());
    }

    @Test
    public void toItemTest() {
        Item itemDto = itemMapper.toItemEntity(ItemRequestDto.builder().name("name").description("desc").available(true).build());
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
    }
}

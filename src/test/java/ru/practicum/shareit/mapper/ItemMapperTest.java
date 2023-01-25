package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

@ExtendWith(MockitoExtension.class)
public class ItemMapperTest {
    private Item item;
    private User user;
    private ItemRequestDto itemRequestDto;

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemMapper itemMapper;

    public ItemMapperTest() {
        this.itemMapper = new ItemMapper(bookingRepository, commentRepository, itemRequestRepository);
    }

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

        this.itemRequestDto = ItemRequestDto.builder()
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    @Test
    public void toItemDtoTest() {
        ItemMainResponseDto itemDto = ItemMapper.toItemMainResponseDto(item);
        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getAvailable(), item.getAvailable());
    }
}

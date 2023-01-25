package ru.practicum.shareit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.jdbc.JdbcTestUtils;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemGetResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@Rollback(false)
@SpringBootTest
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void tearDown() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "bookings", "comments", "items", "users", "requests");
    }

    @Test
    public void testCreateItems() {
        User user = userService.addUser(createUserDto());
        Item itemDto = createItemDto(user);

        Item newItem = itemService.addItem(user.getId(), itemDto);

        Optional<Item> itemOptional = Optional.ofNullable(newItem);

        assertThat(itemOptional)
                .isPresent()
                .hasValueSatisfying(item ->
                        assertThat(item).hasFieldOrPropertyWithValue("id", newItem.getId())
                                .hasFieldOrPropertyWithValue("description", itemDto.getDescription())
                                .hasFieldOrPropertyWithValue("name", itemDto.getName())
                                .hasFieldOrPropertyWithValue("available", itemDto.getAvailable())
                                .hasFieldOrPropertyWithValue("owner", itemDto.getOwner())
                );
    }

    @Test
    public void getAllItemsOwnerTest() {
        User user = userService.addUser(createUserDto());
        Item itemDto = createItemDto(user);
        itemService.addItem(user.getId(), itemDto);
        Collection<ItemGetResponseDto> items = itemService.getItemsByUserId(user.getId(), 0, 100);
        assertEquals(items.size(), 1, "Неверный размер листа с вещами");
    }

    @Test
    public void searchByTextTest() {
        User user = userService.addUser(createUserDto());
        Item itemDto = createItemDto(user);
        itemService.addItem(user.getId(), itemDto);

        Collection<ItemGetResponseDto> items = itemService.getItemsByDescription(user.getId(), "des", 0, 100);
        assertEquals(items.size(), 1, "Неверный поиск по слову");
    }

    @Test
    public void createComment() {
        User user = userService.addUser(createUserDto());
        Item itemDto = createItemDto(user);
        Item item = itemService.addItem(user.getId(), itemDto);
        Booking booking = createBooking(item, user);
        bookingRepository.save(booking);
        Comment comment = new Comment(1L, "супер-класс", item, user, LocalDateTime.now());
        Comment commentResponseDto = itemService
                .addComment(user.getId(), item.getId(), comment);

        Optional<Comment> itemOptional = Optional.ofNullable(commentResponseDto);

        assertThat(itemOptional)
                .isPresent()
                .hasValueSatisfying(commentResponse ->
                        assertThat(commentResponse).hasFieldOrPropertyWithValue("id", commentResponseDto.getId())
                                .hasFieldOrPropertyWithValue("text", "супер-класс")
                                .hasFieldOrPropertyWithValue("author", user)
                );
    }

    private Item createItemDto(User owner) {
        return Item.builder()
                .id(1L)
                .available(true)
                .description("desc")
                .name("name")
                .created(LocalDateTime.now())
                .owner(owner)
                .comments(null)
                .request(null)
                .build();
    }

    private Booking createBooking(Item item, User user) {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.of(2023, 1, 5, 15, 15, 15));
        booking.setEnd(LocalDateTime.of(2023, 1, 6, 15, 15, 15));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.APPROVED);
        return booking;
    }

    private User createUserDto() {
        User userDto = new User();
        userDto.setId(1L);
        userDto.setName("иван");
        userDto.setEmail("yand@yandex.ru");
        return userDto;
    }
}

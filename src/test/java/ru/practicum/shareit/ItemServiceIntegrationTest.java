package ru.practicum.shareit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
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
    ItemService itemService;
    @Autowired
    UserService userService;
    @Autowired
    BookingRepository bookingRepository;
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

//    @Test
//    public void testUpdateItems() {
//        User user = userService.addUser(createUserDto());
//        Item itemDto = createItemDto(user);
//        Item oldItem = itemService.addItem(user.getId(), itemDto);
//        itemDto.setName("newName");
//
//        Optional<Item> itemOptional = Optional.ofNullable(itemService.updateItem(oldItem.getId(),
//                user.getId(), itemDto));
//
//        assertThat(itemOptional)
//                .isPresent()
//                .hasValueSatisfying(item ->
//                        assertThat(item).hasFieldOrPropertyWithValue("id", oldItem.getId())
//                                .hasFieldOrPropertyWithValue("description", itemDto.getDescription())
//                                .hasFieldOrPropertyWithValue("name", itemDto.getName())
//                                .hasFieldOrPropertyWithValue("available", itemDto.getAvailable())
//                                .hasFieldOrPropertyWithValue("owner", itemDto.getOwner())
//                );
//    }

//    @Test
//    public void testUpdateItemsStatus() {
//        User user = userService.addUser(createUserDto());
//        Item itemDto = createItemDto(user);
//        Item oldItem = itemService.addItem(user.getId(), itemDto);
//        itemDto.setName(null);
//        itemDto.setDescription(null);
//        itemDto.setAvailable(false);
//        Optional<Item> itemOptional = Optional.ofNullable(itemService.updateItem(oldItem.getId(),
//                user.getId(), itemDto));
//
//        assertThat(itemOptional)
//                .isPresent()
//                .hasValueSatisfying(item ->
//                        assertThat(item).hasFieldOrPropertyWithValue("id", oldItem.getId())
//                                .hasFieldOrPropertyWithValue("description", oldItem.getDescription())
//                                .hasFieldOrPropertyWithValue("name", oldItem.getName())
//                                .hasFieldOrPropertyWithValue("available", itemDto.getAvailable())
//                                .hasFieldOrPropertyWithValue("owner", itemDto.getOwner())
//                );
//    }

//    @Test
//    public void testUpdateItemsName() {
//        User user = userService.addUser(createUserDto());
//        Item itemDto = createItemDto(user);
//        Item oldItem = itemService.addItem(user.getId(), itemDto);
//        itemDto.setName("new name");
//        itemDto.setDescription(null);
//        itemDto.setAvailable(null);
//        Optional<Item> itemOptional = Optional.ofNullable(itemService.updateItem(oldItem.getId(),
//                user.getId(), itemDto));
//
//        assertThat(itemOptional)
//                .isPresent()
//                .hasValueSatisfying(item ->
//                        assertThat(item).hasFieldOrPropertyWithValue("id", oldItem.getId())
//                                .hasFieldOrPropertyWithValue("description", oldItem.getDescription())
//                                .hasFieldOrPropertyWithValue("name", itemDto.getName())
//                                .hasFieldOrPropertyWithValue("available", oldItem.getAvailable())
//                                .hasFieldOrPropertyWithValue("owner", itemDto.getOwner())
//                );
//    }

//    @Test
//    public void testUpdateItemsDescription() {
//        User user = userService.addUser(createUserDto());
//        Item itemDto = createItemDto(user);
//        Item oldItem = itemService.addItem(user.getId(), itemDto);
//        itemDto.setName(null);
//        itemDto.setDescription("new des");
//        itemDto.setAvailable(null);
//        Optional<Item> itemOptional = Optional.ofNullable(itemService.updateItem(oldItem.getId(),
//                user.getId(), itemDto));
//
//        assertThat(itemOptional)
//                .isPresent()
//                .hasValueSatisfying(item ->
//                        assertThat(item).hasFieldOrPropertyWithValue("id", oldItem.getId())
//                                .hasFieldOrPropertyWithValue("description", itemDto.getDescription())
//                                .hasFieldOrPropertyWithValue("name", oldItem.getName())
//                                .hasFieldOrPropertyWithValue("available", oldItem.getAvailable())
//                                .hasFieldOrPropertyWithValue("owner", itemDto.getOwner())
//                );
//    }

    @Test
    public void getAllItemsOwnerTest() {
        User user = userService.addUser(createUserDto());
        Item itemDto = createItemDto(user);
        Item oldItem = itemService.addItem(user.getId(), itemDto);
        Collection<ItemGetResponseDto> items = itemService.getItemsByUserId(user.getId(), 0, 100);
        assertEquals(items.size(), 1, "Неверный размер листа с вещами");
    }

//    @Test
//    public void getAllItemsWithPaginationTest() {
//        User user = userService.addUser(createUserDto());
//        Item itemDto = createItemDto(user);
//        itemService.addItem(user.getId(), itemDto);
//        Collection<ItemDto> items = itemService.getAllItemsWithPagination(user.getId(), 0, 1);
//        assertEquals(items.size(), 1, "Неверный размер листа с вещами");
//    }

    @Test
    public void searchByTextTest() {
        User user = userService.addUser(createUserDto());
        Item itemDto = createItemDto(user);
        itemService.addItem(user.getId(), itemDto);

        Collection<ItemGetResponseDto> items = itemService.getItemsByDescription(user.getId(), "des", 0, 100);
        assertEquals(items.size(), 1, "Неверный поиск по слову");
    }

//    @Test
//    public void searchItemByTextWithPaginationTest() {
//        User user = userService.addUser(createUserDto());
//        Item itemDto = createItemDto(user);
//        itemService.addItem(user.getId(), itemDto);
//
//        Collection<Item> items = itemService.searchItemByTextWithPagination(user.getId(), 0, 1, "nam");
//        assertEquals(items.size(), 1, "Неверный поиск по слову");
//    }

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

//    @Test
//    public void getRequestErrorCreateItemNotUser() {
//        RequestError er = Assertions.assertThrows(
//                RequestError.class,
//                generateExecutableForNotFoundUser()
//        );
//        assertEquals(HttpStatus.NOT_FOUND, er.getStatus());
//    }
//
//    @Test
//    public void getRequestErrorGetNotFoundItem() {
//        RequestError er = Assertions.assertThrows(
//                RequestError.class,
//                generateExecutableForNotFoundItem()
//        );
//        assertEquals(HttpStatus.NOT_FOUND, er.getStatus());
//    }

//    @Test
//    public void getRequestErrorCreateEmptyComment() {
//        RequestError er = Assertions.assertThrows(
//                RequestError.class,
//                generateExecutableForCreateEmptyComment()
//        );
//        assertEquals(HttpStatus.BAD_REQUEST, er.getStatus());
//    }
//
//    @Test
//    public void getRequestErrorForCreateCommentNotFoundUser() {
//        RequestError er = Assertions.assertThrows(
//                RequestError.class,
//                generateExecutableForCreateCommentNotFoundItem()
//        );
//        assertEquals(HttpStatus.BAD_REQUEST, er.getStatus());
//    }
//
//    @Test
//    public void getRequestErrorForPagination() {
//        RequestError er = Assertions.assertThrows(
//                RequestError.class,
//                generateExecutableForIncorrectPagination()
//        );
//        assertEquals(HttpStatus.BAD_REQUEST, er.getStatus());
//    }

//    @Test
//    public void getRequestErrorForPaginationSearch() {
//        RequestError er = Assertions.assertThrows(
//                RequestError.class,
//                generateExecutableForSearchItemByTextWithPagination()
//        );
//        assertEquals(HttpStatus.BAD_REQUEST, er.getStatus());
//    }
//
//    @Test
//    public void getRequestErrorForCreateCommentNullUser() {
//        RequestError er = Assertions.assertThrows(
//                RequestError.class,
//                generateExecutableForCreateUser()
//        );
//        assertEquals(HttpStatus.BAD_REQUEST, er.getStatus());
//    }
//
//    @Test
//    public void getRequestErrorForAddCommentUserNotBooker() {
//        RequestError er = Assertions.assertThrows(
//                RequestError.class,
//                generateExecutableForAddCommentUserNotBooker()
//        );
//        assertEquals(HttpStatus.BAD_REQUEST, er.getStatus());
//    }

    private Executable generateExecutableForNotFoundItem() {
        User user = userService.addUser(createUserDto());
        return () -> itemService.getItemById(user.getId(), 1L);
    }

    private Executable generateExecutableForNotFoundUser() {
        return () -> itemService.addItem(100L, createItemDto(new User()));
    }

    private Executable generateExecutableForCreateEmptyComment() {
        return () -> {
            User user = userService.addUser(createUserDto());
            Item itemDto = createItemDto(user);
            Item item = itemService.addItem(user.getId(), itemDto);
            Booking booking = createBooking(item, user);
            bookingRepository.save(booking);
            itemService.addComment(user.getId(), item.getId(), new Comment(1L, "супер-класс", item, user, LocalDateTime.now()));
        };
    }

    private Executable generateExecutableForCreateCommentNotFoundItem() {
        return () -> itemService.addComment(10L, 10L, new Comment(1L, "супер-класс", new Item(), new User(), LocalDateTime.now()));
    }

    private Executable generateExecutableForIncorrectPagination() {
        return () -> {
            User user = userService.addUser(createUserDto());
            itemService.getItemsByUserId(user.getId(), -5, 10);
        };
    }

    private Executable generateExecutableForSearchItemByTextWithPagination() {
        return () -> {
            User user = userService.addUser(createUserDto());
            itemService.getItemsByDescription(user.getId(), "nam",-5, 10);
        };
    }

    private Executable generateExecutableForCreateUser() {
        return () -> {
            User user = userService.addUser(createUserDto());
            Item itemDto = createItemDto(user);
            Item item = itemService.addItem(user.getId(), itemDto);
            Booking booking = createBooking(item, user);
            bookingRepository.save(booking);
            itemService.addComment(100L, item.getId(), new Comment(1L, "супер-класс", item, user, LocalDateTime.now()));
        };
    }

    private Executable generateExecutableForAddCommentUserNotBooker() {
        return () -> {
            User user = userService.addUser(createUserDto());
            User newUserDto = createUserDto();
            newUserDto.setEmail("rapapap@mail.ru");
            newUserDto.setId(2L);
            User newUser = userService.addUser(newUserDto);
            Item itemDto = createItemDto(user);
            Item item = itemService.addItem(user.getId(), itemDto);
            Booking booking = createBooking(item, user);
            bookingRepository.save(booking);
            itemService.addComment(newUser.getId(), item.getId(), new Comment(1L, "супер-класс", item, user, LocalDateTime.now()));
        };
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

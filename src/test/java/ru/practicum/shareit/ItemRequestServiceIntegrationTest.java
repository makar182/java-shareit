package ru.practicum.shareit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import ru.practicum.shareit.exception.RequestError;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ItemRequestServiceIntegrationTest {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    private ItemRequestService itemRequestService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;

    @AfterEach
    void tearDown() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "requests", "bookings", "comments", "items", "users");
    }

    @Test
    public void createItemRequestTest() {
        User user = userService.addUser(new User(1L, "Олег", "test@yandex.ru"));
        Item item = createItemDto(user);
        itemService.addItem(user.getId(), item);
        ItemRequest request = new ItemRequest();
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());
        request.setDescription("desc");

        ItemRequest requestDtoCreated = itemRequestService.addItemRequest(request, user.getId());
        Optional<ItemRequest> requestDtoOptional = Optional.ofNullable(requestDtoCreated);

        assertThat(requestDtoOptional).isPresent()
                .hasValueSatisfying(requestDto -> assertThat(requestDto)
                        .hasFieldOrPropertyWithValue("description", request.getDescription()));

        assertEquals(requestDtoCreated.getCreated().truncatedTo(ChronoUnit.SECONDS),
                requestDtoCreated.getCreated().truncatedTo(ChronoUnit.SECONDS),
                "Неверно присвоено время старта");
    }

    @Test
    public void getItemRequestForUserTest() {
        User user = userService.addUser(new User(1L, "Олег", "test@yandex.ru"));
        Item item = createItemDto(user);
        itemService.addItem(user.getId(), item);
        ItemRequest request = new ItemRequest();
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());
        request.setDescription("desc");

        ItemRequest requestDtoCreated = itemRequestService.addItemRequest(request, user.getId());
//        Optional<ItemRequest> requestDtoOptional = Optional.ofNullable(requestDtoCreated);

        Collection<ItemRequest> requestDto = itemRequestService.getItemRequestsByOwner(user.getId());
        assertEquals(requestDto.size(), 1);
    }

    @Test
    public void getAllRequestItemsTest() {
        User user = userService.addUser(new User(1L, "Олег", "test@yandex.ru"));
        Item item = createItemDto(user);
        itemService.addItem(user.getId(), item);
        ItemRequest request = new ItemRequest();
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());
        request.setDescription("desc");

        ItemRequest requestDtoCreated = itemRequestService.addItemRequest(request, user.getId());

        Collection<ItemRequest> requestDtoWithPagination = itemRequestService
                .getItemRequestsByOwner(user.getId());

        assertEquals(requestDtoWithPagination.size(), 1, "Неверное значение списка пагинации");
        assertEquals(new ArrayList<>(requestDtoWithPagination).get(0).getId(), requestDtoCreated.getId(),
                "Неверное значение id");
        assertEquals(new ArrayList<>(requestDtoWithPagination).get(0).getItems(),
                requestDtoCreated.getItems(), "Неверно присвоен список вещей");
        assertEquals(new ArrayList<>(requestDtoWithPagination).get(0).getDescription(),
                requestDtoCreated.getDescription(), "Неверно присвоено описание");
    }

    @Test
    public void getRequestByIdTest() {
        User user = userService.addUser(new User(1L, "Олег", "test@yandex.ru"));
        Item item = createItemDto(user);
        itemService.addItem(user.getId(), item);
        ItemRequest request = new ItemRequest();
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());
        request.setDescription("desc");

        ItemRequest requestDtoCreated = itemRequestService.addItemRequest(request, user.getId());
        ItemRequest resultRequestDto = itemRequestService.getItemRequestById(request.getId(), user.getId());

        assertEquals(resultRequestDto.getId(), requestDtoCreated.getId(),
                "Неверное значение id");
        assertEquals(resultRequestDto.getItems(), requestDtoCreated.getItems(),
                "Неверно присвоен список вещей");
        assertEquals(resultRequestDto.getDescription(),
                requestDtoCreated.getDescription(), "Неверно присвоено описание");
    }

    @Test
    public void get404NotFoundErrorForRequest() {
        RequestError er = Assertions.assertThrows(
                RequestError.class,
                getErrorForNotFoundRequest()
        );
        assertEquals(HttpStatus.NOT_FOUND, er.getStatus());
    }

//    @Test
//    public void get400NotFoundErrorForIncorrectPaginationLimit() {
//        RequestError er = Assertions.assertThrows(
//                RequestError.class,
//                getErrorForIncorrectPaginationLimit()
//        );
//        assertEquals(HttpStatus.BAD_REQUEST, er.getStatus());
//    }

    @Test
    public void getItemsWithPaginationTest() {
        User user = userService.addUser(new User(1L, "Олег", "test@yandex.ru"));
        User userTwo = userService.addUser(new User(2L, "Костя", "test2@yandex.ru"));
        Item itemDto = createItemDto(user);
        Item itemDtoTwo = createItemDto(userTwo);
        itemService.addItem(user.getId(), itemDto);
        itemService.addItem(userTwo.getId(), itemDtoTwo);
        ItemRequest request = new ItemRequest();
        ItemRequest requestTwo = new ItemRequest();
        request.setDescription("desc");
        requestTwo.setDescription("descTwo");
        itemRequestService.addItemRequest(request, user.getId());
        itemRequestService.addItemRequest(requestTwo, userTwo.getId());

        Collection<ItemRequest> requestDtoWithPagination = itemRequestService
                .getItemRequestsByOwner(user.getId());

        assertEquals(requestDtoWithPagination.size(), 1, "Неверное значение списка пагинации");
    }

    private Executable getErrorForNotFoundRequest() {
        User user = userService.addUser(new User(1L, "Олег", "test@yandex.ru"));
        return () -> itemRequestService.getItemRequestsByOwner(user.getId());
    }

//    private Executable getErrorForIncorrectPaginationLimit() {
//        User user = userService.addUser(new User(1L, "Олег", "test@yandex.ru"));
//        return () -> itemRequestService.getItemRequestsByOwner(user.getId());
//    }

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

//    private UserDto createUserDto(String name, String email) {
//        UserDto userDto = new UserDto();
//        userDto.setId(1);
//        userDto.setName(name);
//        userDto.setEmail(email);
//        return userDto;
//    }
}

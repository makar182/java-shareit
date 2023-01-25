package ru.practicum.shareit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class BookingServiceIntegrationTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private BookingRepository bookingRepository;

    @AfterEach
    void tearDown() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "bookings", "comments", "items", "users");
    }

    @Test
    public void testCreateBooking() {
        User owner = userService.addUser(new User(1L, "Олег", "test@yandex.ru"));
        Item item = createItemDto(owner);
        Item itemCreated = itemService.addItem(owner.getId(), item);
        User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
        Booking booking = createBooking(itemCreated, user);

        Booking bookingCreated = bookingService.addBooking(booking, user.getId());
        Optional<Booking> bookingOptional = Optional.ofNullable(bookingCreated);

        assertThat(bookingOptional)
                .isPresent()
                .hasValueSatisfying(bookingDto ->
                        assertThat(bookingDto).hasFieldOrPropertyWithValue("item", itemCreated)
                                .hasFieldOrPropertyWithValue("booker", user)
                                .hasFieldOrPropertyWithValue("status", booking.getStatus())
                );
        assertEquals(bookingCreated.getStart().truncatedTo(ChronoUnit.SECONDS),
                booking.getStart().truncatedTo(ChronoUnit.SECONDS), "Неверно присвоено время старта");
        assertEquals(bookingCreated.getEnd().truncatedTo(ChronoUnit.SECONDS),
                booking.getEnd().truncatedTo(ChronoUnit.SECONDS), "Неверно присвоено время завершения");
    }

    @Test
    public void testReplyToBookingTrue() {
        User owner = userService.addUser(new User(1L, "Олег", "test@yandex.ru"));
        Item item = createItemDto(owner);
        Item itemCreated = itemService.addItem(owner.getId(), item);
        User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
        Booking booking = createBooking(itemCreated, user);
        booking.setStatus(BookingStatus.WAITING);
        Booking bookingCreated = bookingService.addBooking(booking, user.getId());
        Optional<Booking> bookingOptional = Optional
                .ofNullable(bookingService.approveBooking(owner.getId(), bookingCreated.getId(), true));

        assertThat(bookingOptional)
                .isPresent()
                .hasValueSatisfying(bookingDto1 ->
                        assertThat(bookingDto1).hasFieldOrPropertyWithValue("status", BookingStatus.APPROVED)
                );
    }

    @Test
    public void testReplyToBookingFalse() {
        User owner = userService.addUser(new User(1L, "Олег", "test@yandex.ru"));
        Item item = createItemDto(owner);
        Item itemCreated = itemService.addItem(owner.getId(), item);
        User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
        Booking booking = createBooking(itemCreated, user);
        booking.setStatus(BookingStatus.WAITING);

        Booking bookingCreated = bookingService.addBooking(booking, user.getId());
        Optional<Booking> bookingOptional = Optional
                .ofNullable(bookingService.approveBooking(owner.getId(), bookingCreated.getId(), false));

        assertThat(bookingOptional)
                .isPresent()
                .hasValueSatisfying(bookingDto1 ->
                        assertThat(bookingDto1).hasFieldOrPropertyWithValue("status", BookingStatus.REJECTED)
                );
    }

    @Test
    public void getBookingByIdForOwnerOrBooker() {
        User owner = userService.addUser(new User(1L, "Олег", "test@yandex.ru"));
        Item item = createItemDto(owner);
        Item itemCreated = itemService.addItem(owner.getId(), item);
        User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
        Booking booking = createBooking(itemCreated, user);

        Booking bookingCreated = bookingService.addBooking(booking, user.getId());

        Booking bookingDtoResult = bookingService
                .getBookingById(user.getId(), bookingCreated.getId());
        assertEquals(bookingDtoResult.getId(), bookingCreated.getId(), "Неверный ID аренды");
        assertEquals(bookingDtoResult.getBooker(), bookingCreated.getBooker(), "Неверный букер");
        assertEquals(bookingDtoResult.getItem(), bookingCreated.getItem(), "Неверная вещь");
        assertEquals(bookingDtoResult.getStart().truncatedTo(ChronoUnit.SECONDS), bookingCreated.getStart().truncatedTo(ChronoUnit.SECONDS), "Неверное время начала");
        assertEquals(bookingDtoResult.getEnd().truncatedTo(ChronoUnit.SECONDS), bookingCreated.getEnd().truncatedTo(ChronoUnit.SECONDS), "Неверное время окончания");
        assertEquals(bookingDtoResult.getStatus(), bookingCreated.getStatus(), "Неверный статус");
    }

    @Test
    public void getAllBookingsForUserStateAll() {
        User owner = userService.addUser(new User(1L, "Олег", "test@yandex.ru"));
        Item item = createItemDto(owner);
        Item itemCreated = itemService.addItem(owner.getId(), item);
        User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
        Booking booking = createBooking(itemCreated, user);
        bookingService.addBooking(booking, user.getId());

        Collection<Booking> bookings = bookingService.getBookingsByBooker(user.getId(), "ALL", 0, 100);

        assertEquals(bookings.size(), 1, "Неверно получен список");
    }

    @Test
    public void getAllBookingsForUserStateFuture() {
        User owner = userService.addUser(new User(1L, "Олег", "test@yandex.ru"));
        Item item = createItemDto(owner);
        Item itemCreated = itemService.addItem(owner.getId(), item);
        User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
        Booking booking = createBooking(itemCreated, user);

        booking.setStart(LocalDateTime.now().plusDays(5));
        booking.setEnd(LocalDateTime.now().plusDays(6));
        bookingService.addBooking(booking, user.getId());
        Collection<Booking> bookings = bookingService.getBookingsByBooker(user.getId(), "FUTURE", 0, 100);

        assertEquals(bookings.size(), 1, "Неверно получен список");
    }

    @Test
    public void getAllBookingsForUserStateWaiting() {
        User owner = userService.addUser(new User(1L, "Олег", "test@yandex.ru"));
        Item item = createItemDto(owner);
        Item itemCreated = itemService.addItem(owner.getId(), item);
        User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
        Booking booking = createBooking(itemCreated, user);
        booking.setStatus(BookingStatus.WAITING);
        bookingService.addBooking(booking, user.getId());
        Collection<Booking> bookings = bookingService.getBookingsByBooker(user.getId(), "WAITING", 0, 100);

        assertEquals(bookings.size(), 1, "Неверно получен список");
    }

    @Test
    public void getAllBookingsForUserStateRejected() {
        User owner = userService.addUser(new User(1L, "Олег", "test@yandex.ru"));
        Item item = createItemDto(owner);
        Item itemCreated = itemService.addItem(owner.getId(), item);
        User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
        Booking booking = createBooking(itemCreated, user);
        booking.setStatus(BookingStatus.WAITING);
        Booking bookingCreated = bookingService.addBooking(booking, user.getId());

        bookingService.approveBooking(owner.getId(), bookingCreated.getId(), false);

        Collection<Booking> bookings = bookingService
                .getBookingsByBooker(user.getId(), "REJECTED", 1, 100);
        assertEquals(bookings.size(), 1, "Неверно получен список");
    }

    @Test
    public void getAllBookingsForUserStatePast() {
        User owner = userService.addUser(new User(1L, "Олег", "test@yandex.ru"));
        Item item = createItemDto(owner);
        Item itemCreated = itemService.addItem(owner.getId(), item);
        User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
        Booking booking = createBooking(itemCreated, user);

        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));

        bookingService.addBooking(booking, user.getId());

        Collection<Booking> bookings = bookingService
                .getBookingsByBooker(user.getId(), "PAST", 1, 100);
        assertEquals(bookings.size(), 1, "Неверно получен список");
    }

    @Test
    public void getAllBookingsForUserStateCurrent() {
        User owner = userService.addUser(new User(1L, "Олег", "test@yandex.ru"));
        Item item = createItemDto(owner);
        Item itemCreated = itemService.addItem(owner.getId(), item);
        User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
        Booking booking = createBooking(itemCreated, user);

        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));

        bookingService.addBooking(booking, user.getId());

        Collection<Booking> bookings = bookingService
                .getBookingsByBooker(user.getId(), "CURRENT", 1, 100);
        assertEquals(bookings.size(), 1, "Неверно получен список");
    }

    @Test
    public void getAllBookingForOwnerStateAll() {
        User owner = userService.addUser(new User(1L, "Олег", "test@yandex.ru"));
        Item item = createItemDto(owner);
        Item itemCreated = itemService.addItem(owner.getId(), item);
        User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
        Booking booking = createBooking(itemCreated, user);

        bookingService.addBooking(booking, user.getId());

        Collection<Booking> bookings = bookingService
                .getBookingsByOwner(owner.getId(), "ALL", 0, 100);

        assertEquals(bookings.size(), 1, "Неверно получен список");
    }

    @Test
    public void getAllBookingForOwnerStateFuture() {
        User owner = userService.addUser(new User(1L, "Олег", "test@yandex.ru"));
        Item item = createItemDto(owner);
        Item itemCreated = itemService.addItem(owner.getId(), item);
        User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
        Booking booking = createBooking(itemCreated, user);
        booking.setStatus(BookingStatus.WAITING);


        booking.setStart(LocalDateTime.now().plusDays(5));
        booking.setEnd(LocalDateTime.now().plusDays(6));
        bookingService.addBooking(booking, user.getId());

        Collection<Booking> bookings = bookingService
                .getBookingsByOwner(owner.getId(), "FUTURE", 0, 100);

        assertEquals(bookings.size(), 1, "Неверно получен список");
    }

    @Test
    public void getAllBookingsForOwnerStateWaiting() {
        User owner = userService.addUser(new User(1L, "Олег", "test@yandex.ru"));
        Item item = createItemDto(owner);
        Item itemCreated = itemService.addItem(owner.getId(), item);
        User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
        Booking booking = createBooking(itemCreated, user);

        booking.setStatus(BookingStatus.WAITING);
        bookingService.addBooking(booking, user.getId());

        Collection<Booking> bookings = bookingService
                .getBookingsByOwner(owner.getId(), "WAITING", 0, 100);

        assertEquals(bookings.size(), 1, "Неверно получен список");
    }

    @Test
    public void getAllBookingsForOwnerStateRejected() {
        User owner = userService.addUser(new User(1L, "Олег", "test@yandex.ru"));
        Item item = createItemDto(owner);
        Item itemCreated = itemService.addItem(owner.getId(), item);
        User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
        Booking booking = createBooking(itemCreated, user);
        booking.setStatus(BookingStatus.WAITING);
        Booking bookingCreated = bookingService.addBooking(booking, user.getId());

        bookingService.approveBooking(owner.getId(), bookingCreated.getId(), false);

        Collection<Booking> bookings = bookingService
                .getBookingsByOwner(owner.getId(), "REJECTED", 0, 100);
        assertEquals(bookings.size(), 1, "Неверно получен список");
    }

    @Test
    public void getAllBookingsForOwnerStatePast() {
        User owner = userService.addUser(new User(1L, "Олег", "test@yandex.ru"));
        Item item = createItemDto(owner);
        Item itemCreated = itemService.addItem(owner.getId(), item);
        User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
        Booking booking = createBooking(itemCreated, user);

        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        bookingService.addBooking(booking, user.getId());

        Collection<Booking> bookings = bookingService
                .getBookingsByOwner(owner.getId(), "PAST", 0, 100);
        assertEquals(bookings.size(), 1, "Неверно получен список");
    }

    @Test
    public void getAllBookingsForOwnerStateCurrent() {
        User owner = userService.addUser(new User(1L, "Олег", "test@yandex.ru"));
        Item item = createItemDto(owner);
        Item itemCreated = itemService.addItem(owner.getId(), item);
        User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
        Booking booking = createBooking(itemCreated, user);

        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));

        bookingService.addBooking(booking, user.getId());

        Collection<Booking> bookings = bookingService
                .getBookingsByOwner(owner.getId(), "CURRENT", 0, 100);
        assertEquals(bookings.size(), 1, "Неверно получен список");
    }

    @Test
    public void stringToEnumTest() {
        BookingState state = BookingState.ALL;
        BookingState resultState = BookingState.getByString("ALL");
        assertEquals(state, resultState, "Неверная работа конвертера");
    }

    private Booking createBooking(Item item, User user) {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusMinutes(3));
        booking.setEnd(LocalDateTime.now().plusMinutes(30));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.APPROVED);
        return booking;
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
}
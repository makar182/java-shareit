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
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.RequestError;
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
        User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
        Booking booking = createBooking(item, user);

        Booking bookingCreated = bookingService.addBooking(booking, user.getId());
        Optional<Booking> bookingOptional = Optional.ofNullable(bookingCreated);

        assertThat(bookingOptional)
                .isPresent()
                .hasValueSatisfying(bookingDto ->
                        assertThat(bookingDto).hasFieldOrPropertyWithValue("item", item)
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
        User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
        Booking booking = createBooking(item, user);

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
        User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
        Booking booking = createBooking(item, user);

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
        User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
        Booking booking = createBooking(item, user);

        Booking bookingCreated = bookingService.addBooking(booking, user.getId());

        Booking bookingDtoResult = bookingService
                .getBookingById(user.getId(), bookingCreated.getId());
        assertEquals(bookingDtoResult.getId(), bookingCreated.getId(), "Неверный ID аренды");
        assertEquals(bookingDtoResult.getBooker(), bookingCreated.getBooker(), "Неверный букер");
        assertEquals(bookingDtoResult.getItem(), bookingCreated.getItem(), "Неверная вещь");
        assertEquals(bookingDtoResult.getStart(), bookingCreated.getStart(), "Неверное время начала");
        assertEquals(bookingDtoResult.getEnd(), bookingCreated.getEnd(), "Неверное время окончания");
        assertEquals(bookingDtoResult.getStatus(), bookingCreated.getStatus(), "Неверный статус");
    }

    @Test
    public void getAllBookingsForUserStateAll() {
        User owner = userService.addUser(new User(1L, "Олег", "test@yandex.ru"));
        Item item = createItemDto(owner);
        User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
        Booking booking = createBooking(item, user);
        bookingService.addBooking(booking, user.getId());

        Collection<Booking> bookings = bookingService.getBookingsByBooker(user.getId(), "ALL", 0, 100);

        assertEquals(bookings.size(), 1, "Неверно получен список");
    }

    @Test
    public void getAllBookingsForUserStateFuture() {
        User owner = userService.addUser(new User(1L, "Олег", "test@yandex.ru"));
        Item item = createItemDto(owner);
        User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
        Booking booking = createBooking(item, user);

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
        User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
        Booking booking = createBooking(item, user);
        booking.setStatus(BookingStatus.WAITING);
        bookingService.addBooking(booking, user.getId());
        Collection<Booking> bookings = bookingService.getBookingsByBooker(user.getId(), "WAITING", 0, 100);

        assertEquals(bookings.size(), 1, "Неверно получен список");
    }

    @Test
    public void getAllBookingsForUserStateRejected() {
        User owner = userService.addUser(new User(1L, "Олег", "test@yandex.ru"));
        Item item = createItemDto(owner);
        User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
        Booking booking = createBooking(item, user);

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
        User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
        Booking booking = createBooking(item, user);

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
        User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
        Booking booking = createBooking(item, user);

        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));

        bookingService.addBooking(booking, user.getId());

        Collection<Booking> bookings = bookingService
                .getBookingsByBooker(user.getId(), "CURRENT", 1, 100);
        assertEquals(bookings.size(), 1, "Неверно получен список");
    }

    //
//    @Test
//    public void getAllBookingsForUserStateAllWithPagination() {
//        User owner = userService.addUser(new User(1L, "Олег", "test@yandex.ru"));
//        Item item = createItemDto(owner);
//        User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
//        Booking booking = createBooking(item, user);
//
//        Booking bookingCreated = bookingService.addBooking(booking, user.getId());
//
//        Collection<Booking> bookings = bookingService
//                .getBookingsByBooker(user.getId(), "ALL", 1, 100);
//
//        assertEquals(bookings.size(), 1, "Неверно получен список");
//    }
//
//    @Test
//    public void getAllBookingsForUserStateFutureWithPagination() {
//        User owner = userService.addUser(new User(1L, "Олег", "test@yandex.ru"));
//        Item item = createItemDto(owner);
//        User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
//        Booking booking = createBooking(item, user);
//
//        Booking bookingCreated = bookingService.addBooking(booking, user.getId());
//
//        booking.setStart(LocalDateTime.now().plusDays(5));
//        booking.setEnd(LocalDateTime.now().plusDays(6));
//        bookingService.createBooking(booking);
//        Collection<BookingDto> bookings = bookingService
//                .getAllBookingsForUserWithPagination(BookingState.FUTURE, user.getId(), 0, 1);
//
//        assertEquals(bookings.size(), 1, "Неверно получен список");
//    }
//
//    @Test
//    public void getAllBookingsForUserStateWaitingWithPagination() {
//        User owner = userService.addUser(new User(1L, "Олег", "test@yandex.ru"));
//        Item item = createItemDto(owner);
//        User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
//        Booking booking = createBooking(item, user);
//
//        Booking bookingCreated = bookingService.addBooking(booking, user.getId());
//
//        booking.setStatus(Status.WAITING);
//        bookingService.createBooking(booking);
//        Collection<BookingDto> bookings = bookingService
//                .getAllBookingsForUserWithPagination(BookingState.WAITING, user.getId(), 0, 1);
//
//        assertEquals(bookings.size(), 1, "Неверно получен список");
//    }
//
//    @Test
//    public void getAllBookingsForUserStateRejectedWithPagination() {
//        User owner = userService.addUser(new User(1L, "Олег", "test@yandex.ru"));
//        Item item = createItemDto(owner);
//        User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
//        Booking booking = createBooking(item, user);
//
//        Booking bookingCreated = bookingService.addBooking(booking, user.getId());
//
//        BookingDto bookingDto = bookingService.createBooking(booking);
//        bookingService.replyToBooking(owner.getId(), bookingDto.getId(), false);
//
//        Collection<BookingDto> bookings = bookingService
//                .getAllBookingsForUserWithPagination(BookingState.REJECTED, user.getId(), 0, 1);
//        assertEquals(bookings.size(), 1, "Неверно получен список");
//    }
//
//    @Test
//    public void getAllBookingsForUserStatePastWithPagination() {
//        User owner = userService.addUser(new User(1L, "Олег", "test@yandex.ru"));
//        Item item = createItemDto(owner);
//        User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
//        Booking booking = createBooking(item, user);
//
//        Booking bookingCreated = bookingService.addBooking(booking, user.getId());
//
//        booking.setStart(LocalDateTime.now().minusDays(2));
//        booking.setEnd(LocalDateTime.now().minusDays(1));
//        bookingRepository.save(booking);
//
//        Collection<BookingDto> bookings = bookingService
//                .getAllBookingsForUserWithPagination(BookingState.PAST, user.getId(), 0, 1);
//        assertEquals(bookings.size(), 1, "Неверно получен список");
//    }
//
//    @Test
//    public void getAllBookingsForUserStateCurrentWithPagination() {
//        User owner = userService.addUser(new User(1L, "Олег", "test@yandex.ru"));
//        Item item = createItemDto(owner);
//        User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
//        Booking booking = createBooking(item, user);
//
//        Booking bookingCreated = bookingService.addBooking(booking, user.getId());
//
//        booking.setStart(LocalDateTime.now().minusDays(1));
//        booking.setEnd(LocalDateTime.now().plusDays(1));
//        bookingRepository.save(booking);
//
//        Collection<BookingDto> bookings = bookingService
//                .getAllBookingsForUserWithPagination(BookingState.CURRENT, user.getId(), 0, 1);
//        assertEquals(bookings.size(), 1, "Неверно получен список");
//    }
    //

    @Test
    public void getAllBookingForOwnerStateAll() {
        User owner = userService.addUser(new User(1L, "Олег", "test@yandex.ru"));
        Item item = createItemDto(owner);
        User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
        Booking booking = createBooking(item, user);

        bookingService.addBooking(booking, user.getId());

        Collection<Booking> bookings = bookingService
                .getBookingsByOwner(owner.getId(), "ALL", 0, 100);

        assertEquals(bookings.size(), 1, "Неверно получен список");
    }

    @Test
    public void getAllBookingForOwnerStateFuture() {
        User owner = userService.addUser(new User(1L, "Олег", "test@yandex.ru"));
        Item item = createItemDto(owner);
        User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
        Booking booking = createBooking(item, user);



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
        User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
        Booking booking = createBooking(item, user);

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
        User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
        Booking booking = createBooking(item, user);

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
        User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
        Booking booking = createBooking(item, user);

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
        User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
        Booking booking = createBooking(item, user);

        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));

        bookingService.addBooking(booking, user.getId());

        Collection<Booking> bookings = bookingService
                .getBookingsByOwner(owner.getId(), "CURRENT", 0, 100);
        assertEquals(bookings.size(), 1, "Неверно получен список");
    }

    //
//    @Test
//    public void getAllBookingForOwnerStateAllWithPagination() {
//        User owner = userService.addUser(new User(1L, "Олег", "test@yandex.ru"));
//        Item item = createItemDto(owner);
//        User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
//        Booking booking = createBooking(item, user);
//        bookingService.addBooking(booking, user.getId());
//
//        Collection<Booking> bookings = bookingService
//                .getBookingsByOwner(owner.getId(), "CURRENT", 0, 100);
//
//        assertEquals(bookings.size(), 1, "Неверно получен список");
//    }
//
//    @Test
//    public void getAllBookingForOwnerStateFutureWithPagination() {
//        User owner = userService.createUser(createUserDto("игорь", "mail@mail.ru"));
//        ItemDto itemDto = createItemDto(owner);
//        Item item = itemService.createItem(owner.getId(), itemDto);
//        User user = userService.createUser(createUserDto("иван", "yand@yandex.ru"));
//        Booking booking = createBooking(item, user);
//        booking.setStart(LocalDateTime.now().plusDays(5));
//        booking.setEnd(LocalDateTime.now().plusDays(6));
//        bookingService.createBooking(booking);
//        Collection<BookingDto> bookings = bookingService
//                .getAllBookingForOwnerWithPagination(BookingState.FUTURE, owner.getId(), 0, 1);
//
//        assertEquals(bookings.size(), 1, "Неверно получен список");
//    }
//
//    @Test
//    public void getAllBookingsForOwnerStateWaitingWithPagination() {
//        User owner = userService.createUser(createUserDto("игорь", "mail@mail.ru"));
//        ItemDto itemDto = createItemDto(owner);
//        Item item = itemService.createItem(owner.getId(), itemDto);
//        User user = userService.createUser(createUserDto("иван", "yand@yandex.ru"));
//        Booking booking = createBooking(item, user);
//        booking.setStatus(Status.WAITING);
//        bookingService.createBooking(booking);
//        Collection<BookingDto> bookings = bookingService
//                .getAllBookingForOwnerWithPagination(BookingState.WAITING, owner.getId(), 0, 1);
//
//        assertEquals(bookings.size(), 1, "Неверно получен список");
//    }
//
//    @Test
//    public void getAllBookingsForOwnerStateRejectedWithPagination() {
//        User owner = userService.createUser(createUserDto("игорь", "mail@mail.ru"));
//        ItemDto itemDto = createItemDto(owner);
//        Item item = itemService.createItem(owner.getId(), itemDto);
//        User user = userService.createUser(createUserDto("иван", "yand@yandex.ru"));
//        Booking booking = createBooking(item, user);
//        BookingDto bookingDto = bookingService.createBooking(booking);
//        bookingService.replyToBooking(owner.getId(), bookingDto.getId(), false);
//
//        Collection<BookingDto> bookings = bookingService
//                .getAllBookingForOwnerWithPagination(BookingState.REJECTED, owner.getId(), 0, 1);
//        assertEquals(bookings.size(), 1, "Неверно получен список");
//    }
//
//    @Test
//    public void getAllBookingsForOwnerStatePastWithPagination() {
//        User owner = userService.createUser(createUserDto("игорь", "mail@mail.ru"));
//        ItemDto itemDto = createItemDto(owner);
//        Item item = itemService.createItem(owner.getId(), itemDto);
//        User user = userService.createUser(createUserDto("иван", "yand@yandex.ru"));
//        Booking booking = createBooking(item, user);
//        booking.setStart(LocalDateTime.now().minusDays(2));
//        booking.setEnd(LocalDateTime.now().minusDays(1));
//        bookingRepository.save(booking);
//
//        Collection<BookingDto> bookings = bookingService
//                .getAllBookingForOwnerWithPagination(BookingState.PAST, owner.getId(), 0, 1);
//        assertEquals(bookings.size(), 1, "Неверно получен список");
//    }
//
//    @Test
//    public void getAllBookingsForOwnerStateCurrentWithPagination() {
//        User owner = userService.createUser(createUserDto("игорь", "mail@mail.ru"));
//        ItemDto itemDto = createItemDto(owner);
//        Item item = itemService.createItem(owner.getId(), itemDto);
//        User user = userService.createUser(createUserDto("иван", "yand@yandex.ru"));
//        Booking booking = createBooking(item, user);
//        booking.setStart(LocalDateTime.now().minusDays(1));
//        booking.setEnd(LocalDateTime.now().plusDays(1));
//        bookingRepository.save(booking);
//
//        Collection<BookingDto> bookings = bookingService
//                .getAllBookingForOwnerWithPagination(BookingState.CURRENT, owner.getId(), 0, 1);
//        assertEquals(bookings.size(), 1, "Неверно получен список");
//    }

    //
    @Test
    public void stringToEnumTest() {
        BookingState state = BookingState.ALL;
        BookingState resultState = BookingState.getByString("all");
        assertEquals(state, resultState, "Неверная работа конвертера");
    }

    @Test
    public void get400BadRequestIncorrectData() {
        RequestError er = Assertions.assertThrows(
                RequestError.class,
                getErrorForIncorrectDataBooking()
        );
        assertEquals(HttpStatus.BAD_REQUEST, er.getStatus());
    }

    @Test
    public void get404NotFoundRequestBookerIsOwner() {
        RequestError er = Assertions.assertThrows(
                RequestError.class,
                getErrorForBookerIsOwner()
        );
        assertEquals(HttpStatus.NOT_FOUND, er.getStatus());
    }

    @Test
    public void get404NotFoundRequestUserNotOwnerItem() {
        RequestError er = Assertions.assertThrows(
                RequestError.class,
                getErrorForUserNotOwnerItem()
        );
        assertEquals(HttpStatus.NOT_FOUND, er.getStatus());
    }

    @Test
    public void get404NotFoundGetBookingForNotFoundItem() {
        RequestError er = Assertions.assertThrows(
                RequestError.class,
                getErrorGetBookingForNotFoundItem()
        );
        assertEquals(HttpStatus.NOT_FOUND, er.getStatus());
    }

    @Test
    public void get400BadRequestForReplyToBooking() {
        RequestError er = Assertions.assertThrows(
                RequestError.class,
                getErrorForUnsupportedStatusReplyToBooking());
        assertEquals(HttpStatus.BAD_REQUEST, er.getStatus());
    }

    @Test
    public void get400BadRequestForCreateBooking() {
        RequestError er = Assertions.assertThrows(
                RequestError.class,
                getErrorForIncorrectForFalseAvailable());
        assertEquals(HttpStatus.BAD_REQUEST, er.getStatus());
    }

    @Test
    public void get404NotFoundForGetBookingById() {
        RequestError er = Assertions.assertThrows(
                RequestError.class,
                getErrorForGetBookingById());
        assertEquals(HttpStatus.NOT_FOUND, er.getStatus());
    }

    @Test
    public void get400BadRequestForPaginationUser() {
        RequestError error = Assertions.assertThrows(
                RequestError.class,
                getErrorBadRequestForPaginationUser());
        assertEquals(HttpStatus.BAD_REQUEST, error.getStatus());
    }

    @Test
    public void get400BadRequestForPaginationOwner() {
        RequestError error = Assertions.assertThrows(
                RequestError.class,
                getErrorBadRequestForPaginationOwner());
        assertEquals(HttpStatus.BAD_REQUEST, error.getStatus());
    }

    private Executable getErrorForIncorrectDataBooking() {
        return () -> {
            User owner = userService.addUser(new User(1L, "Олег", "test@yandex.ru"));
            Item item = createItemDto(owner);
            User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
            Booking booking = createBooking(item, user);
            booking.setStart(LocalDateTime.now().minusDays(1));
            bookingService.addBooking(booking, user.getId());
        };
    }

    private Executable getErrorForIncorrectForFalseAvailable() {
        return () -> {
            User owner = userService.addUser(new User(1L, "Олег", "test@yandex.ru"));
            Item item = createItemDto(owner);
            User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
            Booking booking = createBooking(item, user);
            booking.setStart(LocalDateTime.now().plusMinutes(10));
            bookingService.addBooking(booking, user.getId());
        };
    }

    private Executable getErrorForBookerIsOwner() {
        return () -> {
            User owner = userService.addUser(new User(1L, "Олег", "test@yandex.ru"));
            Item item = createItemDto(owner);
            User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
            Booking booking = createBooking(item, user);
            bookingService.addBooking(booking, owner.getId());
        };
    }

    private Executable getErrorForUserNotOwnerItem() {
        return () -> {
            User owner = userService.addUser(new User(1L, "Олег", "test@yandex.ru"));
            Item item = createItemDto(owner);
            User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
            Booking booking = createBooking(item, user);

            Booking bookingCreated = bookingService.addBooking(booking, owner.getId());
            bookingService.approveBooking(user.getId(), bookingCreated.getId(), false);
        };
    }

    private Executable getErrorGetBookingForNotFoundItem() {
        return () -> bookingService.getBookingById(1L, 100L);
    }

    private Executable getErrorForUnsupportedStatusReplyToBooking() {
        return () -> {
            User owner = userService.addUser(new User(1L, "Олег", "test@yandex.ru"));
            Item item = createItemDto(owner);
            User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
            Booking booking = createBooking(item, user);
            Booking bookingCreated = bookingService.addBooking(booking, owner.getId());

            bookingService.approveBooking(owner.getId(), bookingCreated.getId(), true);
            bookingService.approveBooking(owner.getId(), bookingCreated.getId(), true);
        };
    }

    private Executable getErrorForGetBookingById() {
        return () -> {
            User owner = userService.addUser(new User(1L, "Олег", "test@yandex.ru"));
            Item item = createItemDto(owner);
            User user = userService.addUser(new User(2L, "Иван", "test1@yandex.ru"));
            Booking booking = createBooking(item, user);
            bookingService.addBooking(booking, owner.getId());

            bookingService.getBookingsByBooker(100L, "ALL", 0,100);
        };
    }

    private Executable getErrorBadRequestForPaginationUser() {
        return () -> bookingService
                .getBookingsByBooker(1L, "ALL",-5, 0);
    }

    private Executable getErrorBadRequestForPaginationOwner() {
        return () -> bookingService
                .getBookingsByOwner(1L, "ALL",-5, 0);
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

//    private User createUserDto(String name, String email) {
//        User user = new User();
//        user.setId(1L);
//        user.setName(name);
//        user.setEmail(email);
//        return user;
//    }
}
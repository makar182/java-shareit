package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public BookingServiceImpl(UserRepository userRepository, BookingRepository bookingRepository, ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
    }


    @Override
    public Booking getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.info(String.format("Бронирование №%d не существует!", bookingId));
            throw new BookingNotExistException(String.format("Бронирования №%d не существует!", bookingId));
        });

        if (!userId.equals(booking.getItem().getOwner().getId())
                && !userId.equals(booking.getBooker().getId())) {
            log.info(String.format("Пользователь №%d не может просматривать бронирование №%d!", userId, bookingId));
            throw new UserNotAllowedToGetBookingException(String.format("Пользователь №%d не может просматривать бронирование №%d!", userId, bookingId));
        }

        return booking;
    }

    @Override
    public List<Booking> getBookingsByBooker(Long userId, String state) {
        checkUserExists(userId);

        BookingState stateEnum = getBookingStateByString(state);

        if (stateEnum.equals(BookingState.ALL)) {
            return bookingRepository.findAllByBooker_Id(userId).stream()
                    .sorted(Comparator.comparing(Booking::getStart).reversed())
                    .collect(Collectors.toList());
        } else if (stateEnum.equals(BookingState.CURRENT)) {
            return bookingRepository.findAllByBooker_Id(userId).stream()
                    .filter(booking -> booking.getStart().isBefore(LocalDateTime.now())
                            && booking.getEnd().isAfter(LocalDateTime.now()))
                    .sorted(Comparator.comparing(Booking::getStart).reversed())
                    .collect(Collectors.toList());
        } else if (stateEnum.equals(BookingState.PAST)) {
            return bookingRepository.findAllByBooker_Id(userId).stream()
                    .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                    .sorted(Comparator.comparing(Booking::getStart).reversed())
                    .collect(Collectors.toList());
        } else if (stateEnum.equals(BookingState.FUTURE)) {
            return bookingRepository.findAllByBooker_Id(userId).stream()
                    .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                    .sorted(Comparator.comparing(Booking::getStart).reversed())
                    .collect(Collectors.toList());
        } else if (stateEnum.equals(BookingState.WAITING)) {
            return bookingRepository.findAllByBooker_Id(userId).stream()
                    .filter(booking -> booking.getStatus().equals(BookingStatus.WAITING))
                    .sorted(Comparator.comparing(Booking::getStart).reversed())
                    .collect(Collectors.toList());
        } else if (stateEnum.equals(BookingState.REJECTED)) {
            return bookingRepository.findAllByBooker_Id(userId).stream()
                    .filter(booking -> booking.getStatus().equals(BookingStatus.REJECTED))
                    .sorted(Comparator.comparing(Booking::getStart).reversed())
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    @Override
    public List<Booking> getBookingsByOwner(Long userId, String state) {
        checkUserExists(userId);

        BookingState stateEnum = getBookingStateByString(state);

        if (stateEnum.equals(BookingState.ALL)) {
            return bookingRepository.findAllByItem_Owner_Id(userId).stream()
                    .sorted(Comparator.comparing(Booking::getStart).reversed())
                    .collect(Collectors.toList());
        } else if (stateEnum.equals(BookingState.CURRENT)) {
            return bookingRepository.findAllByItem_Owner_Id(userId).stream()
                    .filter(booking -> booking.getStart().isBefore(LocalDateTime.now())
                            && booking.getEnd().isAfter(LocalDateTime.now()))
                    .sorted(Comparator.comparing(Booking::getStart).reversed())
                    .collect(Collectors.toList());
        } else if (stateEnum.equals(BookingState.PAST)) {
            return bookingRepository.findAllByItem_Owner_Id(userId).stream()
                    .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                    .sorted(Comparator.comparing(Booking::getStart).reversed())
                    .collect(Collectors.toList());
        } else if (stateEnum.equals(BookingState.FUTURE)) {
            return bookingRepository.findAllByItem_Owner_Id(userId).stream()
                    .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                    .sorted(Comparator.comparing(Booking::getStart).reversed())
                    .collect(Collectors.toList());
        } else if (stateEnum.equals(BookingState.WAITING)) {
            return bookingRepository.findAllByItem_Owner_Id(userId).stream()
                    .filter(booking -> booking.getStatus().equals(BookingStatus.WAITING))
                    .sorted(Comparator.comparing(Booking::getStart).reversed())
                    .collect(Collectors.toList());
        } else if (stateEnum.equals(BookingState.REJECTED)) {
            return bookingRepository.findAllByItem_Owner_Id(userId).stream()
                    .filter(booking -> booking.getStatus().equals(BookingStatus.REJECTED))
                    .sorted(Comparator.comparing(Booking::getStart).reversed())
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    @Override
    public Booking addBooking(Booking booking, Long userId) {
        if (booking.getStart().isAfter(booking.getEnd())) {
            log.info("Дата начала бронирования не может быть позже даты окончания!");
            throw new IncorrectBookingDateException("Дата начала бронирования не может быть позже даты окончания!");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.info(String.format("Пользователя №%d не существует!", userId));
            throw new UserNotExistException(String.format("Пользователя №%d не существует!", userId));
        });

        Item item;
        if (booking.getItem().getName() == null) {
            item = itemRepository.findById(booking.getItem().getId()).orElseThrow(() -> {
                log.info(String.format("Предмета №%d не существует!", booking.getItem().getId()));
                throw new ItemNotExistException(String.format("Предмета №%d не существует!", booking.getItem().getId()));
            });
        } else {
            item = booking.getItem();
        }

        if (item.getAvailable().equals(false)) {
            log.info(String.format("Предмет №%d не доступен для бронирования!", booking.getItem().getId()));
            throw new ItemNotAvailableException(String.format("Предмет №%d не доступен для бронирования!", booking.getItem().getId()));
        }

        if (item.getOwner().getId().equals(userId)) {
            log.info("Владелец предмета не может его арендовать!");
            throw new IncorrectBookerException("Владелец предмета не может его арендовать!");
        }

        Booking newBooking = new Booking(null, booking.getStart(), booking.getEnd(), item, user, booking.getStatus());
        return bookingRepository.saveAndFlush(newBooking);
    }

    @Override
    public Booking approveBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.info(String.format("Бронирование №%d не существует!", bookingId));
            throw new BookingNotExistException(String.format("Бронирование №%d не существует!", bookingId));
        });

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            log.info(String.format("Предмет %s из бронирования №%d не принадлежит пользователю №%d!", ItemMapper.toBookingResponseDto(booking.getItem()), bookingId, userId));
            throw new BookingItemNotOwnedByUserException(String.format("Предмет %s из бронирования №%d не принадлежит пользователю №%d!", ItemMapper.toBookingResponseDto(booking.getItem()), bookingId, userId));
        }

        if (booking.getStatus().equals(BookingStatus.APPROVED) || booking.getStatus().equals(BookingStatus.REJECTED)) {
            log.info(String.format("Бронированию №%d уже присвоен конечный статус!", bookingId));
            throw new BookingChangeStatusException(String.format(String.format("Бронированию №%d уже присвоен конечный статус!", bookingId)));
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        bookingRepository.saveAndFlush(booking);
        return booking;
    }

    private void checkUserExists(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> {
            log.info(String.format("Пользователя №%d не существует!", userId));
            throw new UserNotExistException(String.format("Пользователя №%d не существует!", userId));
        });
    }

    private BookingState getBookingStateByString(String state) {
        try {
            return BookingState.valueOf(state);
        } catch (RuntimeException e) {
            log.info(String.format("Неподдерживаемый статус %s", state));
            throw new UnsupportedBookingStatusException(String.format("Неподдерживаемый статус %s", state));
        }
    }
}

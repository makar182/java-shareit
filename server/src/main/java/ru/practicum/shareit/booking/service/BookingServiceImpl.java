package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

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
    public BookingResponseDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.info(String.format("Бронирование №%d не существует!", bookingId));
            throw new BookingNotExistException(String.format("Бронирования №%d не существует!", bookingId));
        });

        if (!userId.equals(booking.getItem().getOwner().getId()) && !userId.equals(booking.getBooker().getId())) {
            log.info(String.format("Пользователь №%d не может просматривать бронирование №%d!", userId, bookingId));
            throw new UserNotAllowedToGetBookingException(String.format("Пользователь №%d не может просматривать бронирование №%d!", userId, bookingId));
        }

        return BookingMapper.toDto(booking);
    }

    @Override
    public List<BookingResponseDto> getBookingsByBooker(Long userId, String state, int from, int size) {
        checkFromSizeArguments(from, size);

        getUserIfExists(userId);

        BookingState stateEnum = BookingState.getByString(state);

        int page = from == 0 ? 0 : (from / size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("start").descending());

        if (stateEnum.equals(BookingState.ALL)) {
            return BookingMapper.toDtoList(bookingRepository.findAllByBooker_Id(userId, pageable));
        } else if (stateEnum.equals(BookingState.CURRENT)) {
            return BookingMapper.toDtoList(bookingRepository.findAllByBooker_IdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(), LocalDateTime.now(), pageable));
        } else if (stateEnum.equals(BookingState.PAST)) {
            return BookingMapper.toDtoList(bookingRepository.findAllByBooker_IdAndEndIsBefore(userId, LocalDateTime.now(), pageable));
        } else if (stateEnum.equals(BookingState.FUTURE)) {
            return BookingMapper.toDtoList(bookingRepository.findAllByBooker_IdAndStartIsAfter(userId, LocalDateTime.now(), pageable));
        } else if (stateEnum.equals(BookingState.WAITING)) {
            return BookingMapper.toDtoList(bookingRepository.findAllByBooker_IdAndStatus(userId, BookingStatus.WAITING, pageable));
        } else if (stateEnum.equals(BookingState.REJECTED)) {
            return BookingMapper.toDtoList(bookingRepository.findAllByBooker_IdAndStatus(userId, BookingStatus.REJECTED, pageable));
        } else {
            throw new IllegalArgumentException(String.format("Статуса %s не существует", state));
        }
    }

    @Override
    public List<BookingResponseDto> getBookingsByOwner(Long userId, String state, int from, int size) {
        checkFromSizeArguments(from, size);

        getUserIfExists(userId);

        BookingState stateEnum = BookingState.getByString(state);

        int page = from == 0 ? 0 : (from / size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("start").descending());

        if (stateEnum.equals(BookingState.ALL)) {
            return BookingMapper.toDtoList(bookingRepository.findAllByItem_Owner_Id(userId, pageable));
        } else if (stateEnum.equals(BookingState.CURRENT)) {
            return BookingMapper.toDtoList(bookingRepository.findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(), LocalDateTime.now(), pageable));
        } else if (stateEnum.equals(BookingState.PAST)) {
            return BookingMapper.toDtoList(bookingRepository.findAllByItem_Owner_IdAndEndIsBefore(userId, LocalDateTime.now(), pageable));
        } else if (stateEnum.equals(BookingState.FUTURE)) {
            return BookingMapper.toDtoList(bookingRepository.findAllByItem_Owner_IdAndStartIsAfter(userId, LocalDateTime.now(), pageable));
        } else if (stateEnum.equals(BookingState.WAITING)) {
            return BookingMapper.toDtoList(bookingRepository.findAllByItem_Owner_IdAndStatus(userId, BookingStatus.WAITING, pageable));
        } else if (stateEnum.equals(BookingState.REJECTED)) {
            return BookingMapper.toDtoList(bookingRepository.findAllByItem_Owner_IdAndStatus(userId, BookingStatus.REJECTED, pageable));
        } else {
            return null;
        }
    }

    @Override
    public BookingResponseDto addBooking(BookingRequestDto bookingRequestDto, Long userId) {
        Booking booking = BookingMapper.toEntity(bookingRequestDto);

//        if (booking.getStart().isAfter(booking.getEnd())) {
//            log.info("Дата начала бронирования не может быть позже даты окончания!");
//            throw new IncorrectBookingDateException("Дата начала бронирования не может быть позже даты окончания!");
//        }

        User user = getUserIfExists(userId);

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
        return BookingMapper.toDto(bookingRepository.saveAndFlush(newBooking));
    }

    @Override
    public BookingResponseDto approveBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.info(String.format("Бронирование №%d не существует!", bookingId));
            throw new BookingNotExistException(String.format("Бронирование №%d не существует!", bookingId));
        });

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            log.info(String.format("Предмет %s из бронирования №%d не принадлежит пользователю №%d!", booking.getItem(), bookingId, userId));
            throw new BookingItemNotOwnedByUserException(String.format("Предмет %s из бронирования №%d не принадлежит пользователю №%d!", booking.getItem(), bookingId, userId));
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
        return BookingMapper.toDto(booking);
    }

    private User getUserIfExists(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.info(String.format("Пользователя №%d не существует!", userId));
            throw new UserNotExistException(String.format("Пользователя №%d не существует!", userId));
        });
    }

    private void checkFromSizeArguments(int from, int size) {
        if (from < 0 || size <= 0) {
            log.info("Отрицательные значения параметров from и size недопустимы!");
            throw new IllegalArgumentException("Отрицательные значения параметров from и size недопустимы!");
        }
    }
}

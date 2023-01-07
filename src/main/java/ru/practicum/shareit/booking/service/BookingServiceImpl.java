package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingNotExistException;
import ru.practicum.shareit.exception.BookingNotValidPropertiesException;
import ru.practicum.shareit.exception.ItemNotExistException;
import ru.practicum.shareit.exception.UserNotExistException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

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
    public Booking getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.info(String.format("Бронирование №%d не существует!", bookingId));
            throw new BookingNotExistException(String.format("Бронирования №%d не существует!", bookingId));
        });

        if (!userId.equals(booking.getItem().getOwner().getId())
                && !userId.equals(booking.getBooker().getId())) {
            log.info(String.format("Пользователь №%d не может просматривать бронирование №%d!", userId, bookingId));
            throw new BookingNotValidPropertiesException(String.format("Пользователь №%d не может просматривать бронирование №%d!", userId, bookingId));
        }

        return booking;
    }

    @Override
    public List<Booking> getBookingsByUser(Long userId, BookingState state) {
        return null;
    }

    @Override
    public List<Booking> getBookingsByOwner(Long userId, BookingState state) {
        return null;
    }

    @Override
    public Booking addBooking(Booking booking, Long userId) {
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
            log.info(String.format("Предмет %s из бронирования №%d не принадлежит пользователю №%d!", ItemMapper.toDtoForBookingResponse(booking.getItem()), bookingId, userId));
            throw new BookingNotValidPropertiesException(String.format("Предмет %s из бронирования №%d не принадлежит пользователю №%d!", ItemMapper.toDtoForBookingResponse(booking.getItem()), bookingId, userId));
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        bookingRepository.saveAndFlush(booking);
        return booking;
    }
}

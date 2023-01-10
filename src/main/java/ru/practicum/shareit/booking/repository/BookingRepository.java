package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBooker_Id(Long bookerId, Sort start);

    List<Booking> findAllByBooker_IdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime start, LocalDateTime end, Sort by);

    List<Booking> findAllByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort start);

    List<Booking> findAllByBooker_IdAndStartIsAfter(Long bookerId, LocalDateTime start, Sort by);

    List<Booking> findAllByBooker_IdAndStatus(Long bookerId, BookingStatus status, Sort start);


    List<Booking> findAllByItem_Owner_Id(Long ownerId, Sort start);

    List<Booking> findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime start, LocalDateTime end, Sort by);

    List<Booking> findAllByItem_Owner_IdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort start);

    List<Booking> findAllByItem_Owner_IdAndStartIsAfter(Long bookerId, LocalDateTime start, Sort by);

    List<Booking> findAllByItem_Owner_IdAndStatus(Long bookerId, BookingStatus status, Sort start);

    List<Booking> findAllByItem_Id(Long itemId);
}

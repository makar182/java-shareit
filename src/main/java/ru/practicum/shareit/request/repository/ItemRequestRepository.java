package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequester_Id(Long requesterId, Sort sortBy);

    @Query("SELECT ir FROM ItemRequest ir WHERE ir.requester.id <> ?1")
    List<ItemRequest> findAllForVisitor(Long userId, Pageable pageable);
}

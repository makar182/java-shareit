package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByUserId(Long userId);

    List<Item> findAllByDescriptionLike(String description);

//    Optional<Item> addItem(Item item);
//
//    Optional<Item> updateItem(Item item);
//
//    Optional<Item> getItemById(long itemId);
//
//    List<Item> getItemsByUserId(long userId);
//
//    List<Item> getItemsByDescription(String itemDescription);
}

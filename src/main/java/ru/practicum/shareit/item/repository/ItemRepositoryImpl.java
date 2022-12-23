package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    Map<Long, Item> items = new HashMap<>();
    AtomicLong atomicLong = new AtomicLong();

    @Override
    public Optional<Item> addItem(Item item) {
        item.setId(atomicLong.addAndGet(1));
        items.put(item.getId(), item);
        return Optional.of(item);
    }

    @Override
    public Optional<Item> updateItem(Item item) {
        items.put(item.getId(), item);
        return Optional.of(item);
    }

    @Override
    public Optional<Item> getItemById(long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public List<Item> getItemsByUserId(long userId) {
        return items.values().stream()
                .filter(x -> x.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getItemsByDescription(String itemDescription) {
        return items.values().stream()
                .filter(x -> x.getAvailable().equals(true))
                .filter(x -> x.getDescription().toUpperCase().contains(itemDescription.toUpperCase()))
                .collect(Collectors.toList());
    }
}
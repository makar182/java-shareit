package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.interfaces.OnAdd;
import ru.practicum.shareit.interfaces.OnUpdate;
import ru.practicum.shareit.item.dto.*;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @Validated(OnAdd.class) @RequestBody ItemRequestDto itemRequestDto) {
        log.info("addItem by user {} with itemRequestDto {}", userId, itemRequestDto);
        return itemClient.addItem(userId, itemRequestDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable("itemId") Long itemId,
                                          @Validated(OnUpdate.class) @RequestBody ItemRequestDto itemRequestDto) {
        log.info("updateItem by user {} with itemId {} and itemRequestDto {}", userId, itemId, itemRequestDto);
        return itemClient.updateItem(userId, itemId, itemRequestDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable("itemId") long itemId) {
        log.info("getItemById by user {} with itemId {}", userId, itemId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                                     @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        log.info("getItemsByUserId by user {} with from {} and size {}", userId, from, size);
        return itemClient.getItemsByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsByDescription(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                          @RequestParam("text") String itemDescription,
                                                          @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                                          @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        log.info("getItemsByDescription by user {} with itemDescription {}, from {} and size {}", userId, itemDescription, from, size);
        return itemClient.getItemsByDescription(userId, itemDescription, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable("itemId") Long itemId,
                                         @Validated @RequestBody CommentRequestDto commentRequestDto) {
        log.info("addComment by user {} with itemId {} and commentRequestDto {}", userId, itemId, commentRequestDto);
        return itemClient.addComment(userId, itemId, commentRequestDto);
    }

}
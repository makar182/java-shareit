package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.interfaces.OnAdd;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.constraints.Min;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Slf4j
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @Validated(OnAdd.class)  @RequestBody ItemRequestDto itemRequestDto) {
        log.info("addItemRequest by user {} with ItemRequestDto {}", userId, itemRequestDto);
        return requestClient.addItemRequest(userId, itemRequestDto);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable("requestId") Long requestId) {
        log.info("getItemRequestById by user {} with requestId {}", userId, requestId);
        return requestClient.getItemRequestById(userId, requestId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("getItemRequestsByOwner by user {}", userId);
        return requestClient.getItemRequestsByOwner(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getItemRequestsByVisitor(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                          @RequestParam(name = "from", required = false, defaultValue = "0") @Min(0) int from,
                                                           @Min(1) @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        log.info("getItemRequestsByVisitor by user {} with from {} and size {}", userId, from, size);
        return requestClient.getItemRequestsByVisitor(userId, from, size);
    }
}

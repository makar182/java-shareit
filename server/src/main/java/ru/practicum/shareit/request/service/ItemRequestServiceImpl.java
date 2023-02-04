package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemRequestNotExistExceptionException;
import ru.practicum.shareit.exception.UserNotExistException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemResponseDto addItemRequest(ItemRequestDto itemRequestDto, Long userId) {
        User user = getUserIfExists(userId);
        ItemRequest itemRequest = ItemRequestMapper.toEntity(itemRequestDto);
        itemRequest.setRequester(user);
        return ItemRequestMapper.toDto(itemRequestRepository.saveAndFlush(itemRequest));
    }

    @Override
    public ItemResponseDto getItemRequestById(Long itemRequestId, Long userId) {
        getUserIfExists(userId);

        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId).orElseThrow(() -> {
            log.info(String.format("Запроса №%d не существует!", itemRequestId));
            throw new ItemRequestNotExistExceptionException(String.format("Запроса №%d не существует!", itemRequestId));
        });

        return ItemRequestMapper.toDto(itemRequest);
    }

    @Override
    public List<ItemResponseDto> getItemRequestsByOwner(Long userId) {
        getUserIfExists(userId);

        Sort sortBy = Sort.by(Sort.Direction.DESC, "created");
        return ItemRequestMapper.toDtoList(itemRequestRepository.findAllByRequester_Id(userId, sortBy));
    }

    @Override
    public List<ItemResponseDto> getItemRequestsByVisitor(Long userId, Integer from, Integer size) {
        checkFromSizeArguments(from, size);

        getUserIfExists(userId);

        int page = from == 0 ? 0 : (from / size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("created").descending());
        return ItemRequestMapper.toDtoList(itemRequestRepository.findAllForVisitor(userId, pageable));
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

package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemRequestNotExistExceptionException;
import ru.practicum.shareit.exception.UserNotExistException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    public ItemRequestServiceImpl(UserRepository userRepository, ItemRequestRepository itemRequestRepository) {
        this.userRepository = userRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Override
    public ItemRequest addItemRequest(ItemRequest item, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.info(String.format("Пользователя №%d не существует!", userId));
            throw new UserNotExistException(String.format("Пользователя №%d не существует!", userId));
        });

        item.setRequester(user);
        return itemRequestRepository.saveAndFlush(item);
    }

    @Override
    public ItemRequest getItemRequestById(Long itemRequestId, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> {
            log.info(String.format("Пользователя №%d не существует!", userId));
            throw new UserNotExistException(String.format("Пользователя №%d не существует!", userId));
        });

        return itemRequestRepository.findById(itemRequestId).orElseThrow(() -> {
            log.info(String.format("Запроса №%d не существует!", itemRequestId));
            throw new ItemRequestNotExistExceptionException(String.format("Запроса №%d не существует!", itemRequestId));
        });
    }

    @Override
    public List<ItemRequest> getItemRequestsByOwner(Long userId) {
        Sort sortBy = Sort.by(Sort.Direction.DESC, "created");
        return itemRequestRepository.findAllByRequester_Id(userId, sortBy);
    }

    @Override
    public List<ItemRequest> getItemRequestsByVisitor(Long userId, Integer from, Integer size) {
        Sort sortBy = Sort.by(Sort.Direction.DESC, "created");
        return itemRequestRepository.findAll(sortBy);
    }
}

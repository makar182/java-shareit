package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateUserEmailException;
import ru.practicum.shareit.exception.UserNotExistException;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserResponseDto getUserById(Long userId) {
        User user = getUserIfExists(userId);
        log.info(String.format("Пользователь %s выгружен по id.", user));
        return UserMapper.toDto(user);
    }

    @Override
    public List<UserResponseDto> getUsers() {
        List<User> users = userRepository.findAll();
        log.info("Выгружены все пользователи");
        return UserMapper.toDtoList(users);
    }

    @Override
    public UserResponseDto addUser(UserRequestDto userRequestDto) {
        User user = UserMapper.toEntity(userRequestDto);
        User result = userRepository.saveAndFlush(user);
        log.info(String.format("Пользователь %s добавлен.", result));
        return UserMapper.toDto(result);
    }

    @Override
    public UserResponseDto updateUser(Long userId, UserRequestDto userRequestDto) {
        User user = UserMapper.toEntity(userRequestDto);
        User oldUser = getUserIfExists(userId);

        if (user.getEmail() != null
                && !user.getEmail().isBlank()
                && !oldUser.getEmail().equals(user.getEmail())
                && userRepository.existsByEmail(user.getEmail())
        ) {
            log.info(String.format("Электронная почта %s не принадлежит пользователю и она занята другим пользователем!", user.getEmail()));
            throw new DuplicateUserEmailException(String.format("Электронная почта %s не принадлежит пользователю и она занята другим пользователем!", user.getEmail()));
        }

        String newEmail;
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            newEmail = user.getEmail();
        } else {
            newEmail = oldUser.getEmail();
        }

        String newName;
        if (user.getName() != null && !user.getName().isBlank()) {
            newName = user.getName();
        } else {
            newName = oldUser.getName();
        }

        User result = new User(oldUser.getId(), newName, newEmail);
        User newUser = userRepository.saveAndFlush(result);

        if (newUser.getId() == null) {
            log.info(String.format("Пользователь %s не обновлен", result));
            return null;
        }

        log.info(String.format("Пользователь %s обновлен.", newUser));
        return UserMapper.toDto(newUser);
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
        log.info(String.format("Пользователь %d удалён.", userId));
    }

    private User getUserIfExists(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.info(String.format("Пользователя №%d не существует!", userId));
            throw new UserNotExistException(String.format("Пользователя №%d не существует!", userId));
        });
    }
}

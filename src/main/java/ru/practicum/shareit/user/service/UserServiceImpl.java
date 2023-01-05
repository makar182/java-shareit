package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateUserEmailException;
import ru.practicum.shareit.exception.UserNotExistException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUserById(Long userId) {
        User user = userRepository.getReferenceById(userId);
        if (user.getId() == null) {
            log.info(String.format("Пользователь %d не найден", userId));
            return null;
        }
        ;
        log.info(String.format("Пользователь %s выгружен по id.", user));
        return user;
    }

    @Override
    public List<User> getUsers() {
        List<User> users = userRepository.findAll();
        log.info("Выгружены все пользователи");
        return users;
    }

    @Override
    public User addUser(User user) {
//        if (userRepository.checkUserEmailBusy(user.getEmail())) {
//            log.info(String.format("Пользователь с почтой %s уже зарегистрирован", user.getEmail()));
//            throw new DuplicateUserEmailException(String.format("Пользователь с почтой %s уже зарегистрирован", user.getEmail()));
//        }

        User result = userRepository.saveAndFlush(user);
        if (result.getId() == null) {
            log.info(String.format("Пользователь %s не добавлен", user));
            return null;
        }
        ;
        log.info(String.format("Пользователь %s добавлен.", result));
        return result;
    }

    @Override
    public User updateUser(Long userId, User user) {
        User oldUser = userRepository.getReferenceById(userId);

        if (oldUser.getId() == null) {
            log.info(String.format("Пользователь %d не существует!", userId));
            throw new UserNotExistException(String.format("Пользователь %d не существует!", userId));
        }
        ;

        if (user.getEmail() != null
                && !user.getEmail().isBlank()
                && !oldUser.getEmail().equals(user.getEmail())
                //&& userRepository.checkUserEmailBusy(user.getEmail())
        ) {
            log.info(String.format("Электронная почта %s уже зарегистрирована другим пользователем", user.getEmail()));
            throw new DuplicateUserEmailException(String.format("Электронная почта %s уже зарегистрирована другим пользователем", user.getEmail()));

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
        };

        log.info(String.format("Пользователь %s обновлен.", newUser));
        return newUser;
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
        log.info(String.format("Пользователь %d удалён.", userId));
    }
}

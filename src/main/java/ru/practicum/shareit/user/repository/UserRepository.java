package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserRepository {

    User getUserById(Long userId);

    List<User> getUsers();

    Optional<Map<Long, String>> checkUserEmailBusy(String email);

    User addUser(User user);

    User updateUser(User user);

    void deleteUser(Long userId);
}

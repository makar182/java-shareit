package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    Optional<User> getUserById(Long userId);

    List<User> getUsers();

    Boolean checkUserEmailBusy(String email);

    Optional<User> addUser(User user);

    Optional<User> updateUser(User user);

    void deleteUser(Long userId);
}

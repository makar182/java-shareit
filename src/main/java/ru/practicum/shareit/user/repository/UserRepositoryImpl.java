package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UserRepositoryImpl implements UserRepository {
    Map<Long, User> users = new HashMap<>();
    Set<String> emails = new HashSet<>();
    AtomicLong atomicLong = new AtomicLong();

    @Override
    public Optional<User> getUserById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Boolean checkUserEmailBusy(String email) {
        return emails.contains(email);
    }

    @Override
    public Optional<User> addUser(User user) {
        emails.add(user.getEmail());

        user.setId(atomicLong.addAndGet(1));
        users.put(user.getId(), user);
        return Optional.of(user);
    }

    @Override
    public Optional<User> updateUser(User user) {
        emails.remove(users.get(user.getId()).getEmail());
        emails.add(user.getEmail());

        users.put(user.getId(), user);
        return Optional.of(user);
    }

    @Override
    public void deleteUser(Long userId) {
        emails.remove(users.get(userId).getEmail());
        users.remove(userId);
    }
}

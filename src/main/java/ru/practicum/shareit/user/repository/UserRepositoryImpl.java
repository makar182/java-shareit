package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UserRepositoryImpl implements UserRepository{
    Map<Long, User> users;
    Map<Long, String> emails;
    AtomicLong atomicLong;

    public UserRepositoryImpl() {
        users = new HashMap<>();
        emails = new HashMap<>();
        atomicLong = new AtomicLong();
    }

    @Override
    public User getUserById(Long userId) {
        return users.get(userId);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<Map<Long, String>> checkUserEmailBusy(String email) {
        if(emails.containsValue(email)) {
            for(Long userId : emails.keySet()) {
                if(emails.get(userId).equals(email)) {
                    return Optional.of(Map.of(userId, email));
                }
            }
        } else {
            return Optional.empty();
        }
        return Optional.empty();
    }

    @Override
    public User addUser(User user) {
        user.setId(atomicLong.addAndGet(1));
        emails.put(user.getId(), user.getEmail());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        emails.put(user.getId(), user.getEmail());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(Long userId) {
        emails.remove(userId);
        users.remove(userId);
    }
}

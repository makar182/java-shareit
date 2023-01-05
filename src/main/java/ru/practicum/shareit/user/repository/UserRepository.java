package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

//    Optional<User> getUserById(Long userId);
//
//    List<User> getUsers();
//
//    Boolean checkUserEmailBusy(String email);
//
//    Optional<User> addUser(User user);
//
//    Optional<User> updateUser(User user);
//
//    void deleteUser(Long userId);
}

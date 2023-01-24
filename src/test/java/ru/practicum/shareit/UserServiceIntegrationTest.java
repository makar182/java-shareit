package ru.practicum.shareit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UserServiceIntegrationTest {

    @Autowired
    UserService userService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void tearDown() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "users");
    }

    @Test
    public void testCreateUser() {
        User userTest = userService.addUser(createUserDto("иван"));

        Optional<User> userOptional = Optional.ofNullable(userService.getUserById(userTest.getId()));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", userTest.getId())
                                .hasFieldOrPropertyWithValue("name", userTest.getName())
                                .hasFieldOrPropertyWithValue("email", userTest.getEmail())
                );
    }

    @Test
    public void testUpdateUser() {
        User userTest = userService.addUser(createUserDto("иван"));
        User userDto = (createUserDto("олег"));
        userDto.setId(userTest.getId());
        Optional<User> userOptional = Optional.ofNullable(userService.updateUser(userDto.getId(), userDto));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", userTest.getId())
                                .hasFieldOrPropertyWithValue("name", userDto.getName())
                                .hasFieldOrPropertyWithValue("email", userDto.getEmail())
                );
    }

    @Test
    public void testDeleteUserById() {
        User userTest = userService.addUser(createUserDto("иван"));
        userService.deleteUser(userTest.getId());

        assertEquals(userService.getUsers().size(), 0, "Пользователь не удален");
    }

//    @Test
//    public void getRequestErrorForNotFoundUser() {
//        RequestError er = Assertions.assertThrows(
//                RequestError.class,
//                generateExecutableForDeleteNotFoundUser()
//        );
//        assertEquals(HttpStatus.BAD_REQUEST, er.getStatus());
//    }

//    @Test
//    public void getRequestErrorForUpdateNotFoundUser() {
//        RequestError er = Assertions.assertThrows(
//                RequestError.class,
//                generateExecutableForUpdateNotFoundUser()
//        );
//        assertEquals(HttpStatus.BAD_REQUEST, er.getStatus());
//    }

    private Executable generateExecutableForDeleteNotFoundUser() {
        return () -> userService.deleteUser(1L);

    }

    private Executable generateExecutableForUpdateNotFoundUser() {
        return () -> userService.updateUser(1000L, new User());
    }

    private User createUserDto(String name) {
        User userDto = new User();
        userDto.setId(1L);
        userDto.setName(name);
        userDto.setEmail("yand@yandex.ru");
        return userDto;
    }
}

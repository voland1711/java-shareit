package ru.practicum.shareit.user.Sevice;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.user.model.UserMapper.toUser;

@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceTest {
    private final UserServiceImpl userService;
    private UserDto userDto1;
    private UserDto userDto2;
    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private User user5;


    @BeforeEach
    void setup() {
        user1 = User.builder()
                .id(1L)
                .name("nameUser1")
                .email("nameuser1@user.ru")
                .build();

        user2 = User.builder()
                .id(2L)
                .name("nameUser2")
                .email("nameuser2@user.ru")
                .build();
        userDto1 = UserDto.builder()
                .name("nameUser1")
                .email("nameuser1@user.ru")
                .build();
        userDto2 = UserDto.builder()
                .name("nameUser2")
                .email("nameuser2@user.ru")
                .build();
    }

    @Test
    @DisplayName("Создание пользователя, данные заполненны валидными данными")
    public void createUserTest() {
        assertThatNoException().isThrownBy(() -> userService.createUser(userDto1));
        assertEquals(user1, toUser(userService.getUserById(1L)));
    }

    @Test
    @DisplayName("Поле 'name' не является валидным")
    public void createUserNameNotValidTest() {
        userDto1.setName("");
        assertThatThrownBy(() -> userService.createUser(userDto1))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("не должно быть пустым")
                .extracting(e -> ((ConstraintViolationException) e).getConstraintViolations())
                .satisfies(violations -> {
                    assertThat(violations.size()).isEqualTo(1);
                    assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("name");
                });
    }

    @Test
    @DisplayName("Поле 'email' является пустым")
    public void createUserEmailNotValidTest() {
        userDto1.setEmail("name");
        assertThatThrownBy(() -> userService.createUser(userDto1))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("must be a well-formed email address")
                .extracting(e -> ((ConstraintViolationException) e).getConstraintViolations())
                .satisfies(violations -> {
                    assertThat(violations.size()).isEqualTo(1);
                    assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("email");
                });
    }

    @Test
    @DisplayName("Пользовательс id: 1 не существует")
    public void getUserByIdNotFoundTest() {
        assertThatThrownBy(() -> userService.getUserById(1L))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Пользователь с id: 1 не найден");
    }

    @Test
    @DisplayName("Пользователь с id: 1 существует")
    public void getUserByIdTest() {
        UserDto userDto = userService.createUser(userDto1);

        assertEquals(userService.getUserById(userDto.getId()).getName(), userDto1.getName());
        assertEquals(userService.getUserById(userDto.getId()).getEmail(), userDto1.getEmail());

        assertEquals(user1, toUser(userDto));
    }

    @Test
    @DisplayName("Получаем список пользователей")
    public void getAllUsersTest() {
        assertEquals(userService.getAllUsers().size(), 0);

        userService.createUser(userDto1);
        userService.createUser(userDto2);
        assertEquals(userService.getAllUsers().size(), 2);
        List<UserDto> userDtoList = userService.getAllUsers();
        assertEquals(new HashSet<>(userDtoList.stream()
                .map(UserMapper::toUser)
                .collect(Collectors.toList())), new HashSet<>(Arrays.asList(user1, user2)));
    }

    @Test
    @DisplayName("Обновляем данные пользователя")
    public void updateUserTest() {
        userService.createUser(userDto1);
        assertEquals(user1, toUser(userService.getUserById(1L)));
        userService.updateUser(userDto2, 1L);

        assertThat(userService.getUserById(1L).getName()).isEqualTo(user2.getName());
        assertThat(userService.getUserById(1L).getEmail()).isEqualTo(user2.getEmail());
    }

    @Test
    @DisplayName("Удаляем пользователя с id: 1")
    public void deleteUserTest() {
        userService.createUser(userDto1);
        assertEquals(user1, toUser(userService.getUserById(1L)));

        userService.deleteUser(1L);
        assertThatThrownBy(() -> userService.getUserById(1L))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Пользователь с id: 1 не найден");
    }

}

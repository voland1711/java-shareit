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
    private UserDto firstUserDto;
    private UserDto secondUserDto;
    private User firstUser;
    private User secondUser;

    @BeforeEach
    void setup() {
        firstUser = createFirstUser();

        secondUser = createSecondUser();
        firstUserDto = createFirstUserDto();
        secondUserDto = createSecondUserDto();
    }

    private User createFirstUser() {
        return User.builder()
                .id(1L)
                .name("nameFirstUser")
                .email("nameFirstUser@user.ru")
                .build();
    }

    private User createSecondUser() {
        return User.builder()
                .id(2L)
                .name("nameSecondUser")
                .email("nameSecondUser@user.ru")
                .build();
    }

    private UserDto createFirstUserDto() {
        return UserDto.builder()
                .id(1L)
                .name("nameFirstUser")
                .email("nameFirstUser@user.ru")
                .build();
    }

    private UserDto createSecondUserDto() {
        return UserDto.builder()
                .id(2L)
                .name("nameSecondUser")
                .email("nameSecondUser@user.ru")
                .build();
    }
    
    @Test
    @DisplayName("Создание пользователя, данные заполненны валидными данными")
    public void createUserTest() {
        assertThatNoException().isThrownBy(() -> userService.createUser(firstUserDto));
        assertEquals(firstUser, toUser(userService.getUserById(1L)));
    }

    @Test
    @DisplayName("Поле 'name' не является валидным")
    public void createUserNameNotValidTest() {
        firstUserDto.setName("");
        assertThatThrownBy(() -> userService.createUser(firstUserDto))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("name")
                .hasMessageContaining("javax.validation.constraints.NotBlank.message")
                .extracting(e -> ((ConstraintViolationException) e).getConstraintViolations())
                .satisfies(violations -> {
                    assertThat(violations.size()).isEqualTo(1);
                    assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("name");
                });
    }

    @Test
    @DisplayName("Поле 'email' является пустым")
    public void createUserEmailNotValidTest() {
        firstUserDto.setEmail("name");
        assertThatThrownBy(() -> userService.createUser(firstUserDto))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("javax.validation.constraints.Email.message")
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
        UserDto userDto = userService.createUser(firstUserDto);

        assertEquals(userService.getUserById(userDto.getId()).getName(), firstUserDto.getName());
        assertEquals(userService.getUserById(userDto.getId()).getEmail(), firstUserDto.getEmail());

        assertEquals(firstUser, toUser(userDto));
    }

    @Test
    @DisplayName("Получаем список пользователей")
    public void getAllUsersTest() {
        assertEquals(userService.getAllUsers().size(), 0);
        userService.createUser(firstUserDto);
        userService.createUser(secondUserDto);
        assertEquals(userService.getAllUsers().size(), 2);
        List<UserDto> userDtoList = userService.getAllUsers();
        assertEquals(new HashSet<>(userDtoList.stream()
                .map(UserMapper::toUser)
                .collect(Collectors.toList())), new HashSet<>(Arrays.asList(firstUser, secondUser)));
    }

    @Test
    @DisplayName("Обновляем данные пользователя")
    public void updateUserTest() {
        userService.createUser(firstUserDto);
        assertEquals(firstUser, toUser(userService.getUserById(1L)));
        userService.updateUser(secondUserDto, 1L);

        assertThat(userService.getUserById(1L).getName()).isEqualTo(secondUser.getName());
        assertThat(userService.getUserById(1L).getEmail()).isEqualTo(secondUser.getEmail());
    }

    @Test
    @DisplayName("Удаляем пользователя с id: 1")
    public void deleteUserTest() {
        userService.createUser(firstUserDto);
        assertEquals(firstUser, toUser(userService.getUserById(1L)));

        userService.deleteUser(1L);
        assertThatThrownBy(() -> userService.getUserById(1L))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Пользователь с id: 1 не найден");
    }

}

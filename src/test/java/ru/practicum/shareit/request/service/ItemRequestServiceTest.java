package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.user.model.UserMapper.toUser;

@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestServiceTest {
    private final ItemService itemService;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestService itemRequestService;
    private final UserRepository userRepository;
    private ItemRequestDto itemRequestDto1;
    private UserDto userDto1;
    private ItemRequestResponseDto itemRequestResponseDto1;
    private ItemRequest itemRequest;

    @BeforeEach
    void setup() {
        itemRequestDto1 = ItemRequestDto.builder()
                .description("Хотел бы воспользоваться description1")
                .build();

        userDto1 = UserDto.builder()
                .name("nameUser1")
                .email("nameuser1@user.ru")
                .build();

        itemRequestResponseDto1 = ItemRequestResponseDto.builder()
                .id(1L)
                .description("Хотел бы воспользоваться description1")
                .build();
    }


    @Test
    @DisplayName("Создаем ItemRequest, user not found")
    public void createItemRequestUserNotFoundTest() {
        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> itemRequestService.createItemRequest(1L, itemRequestDto1))
                .withMessage("Пользователь с id = 1  не найден");
    }

    @Test
    @DisplayName("Создаем ItemRequest, данные валидны")
    public void createItemRequestTest() {
        userRepository.save(toUser(userDto1));
        ItemRequestResponseDto itemRequestResponseDto = itemRequestService.createItemRequest(1L, itemRequestDto1);
        assertThat(itemRequestResponseDto)
                .isNotNull()
                .extracting(ItemRequestResponseDto::getDescription)
                .isEqualTo(itemRequestDto1.getDescription());
    }

    @Test
    @DisplayName("Create ItemRequest, description is blank")
    public void createItemRequestDescriptionIsBlankTest() {
        itemRequest = ItemRequest.builder().build();
        itemRequest.setRequester(userRepository.save(toUser(userDto1)));
        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> itemRequestRepository.save(itemRequest))
                .satisfies(exception -> {
                    ConstraintViolation<?> violation = exception.getConstraintViolations().iterator().next();
                    String propertyPath = violation.getPropertyPath().toString();
                    String interpolatedMessage = violation.getMessage();
                    assertEquals("description", propertyPath);
                    assertEquals("must not be blank", interpolatedMessage);
                });
    }

    @Test
    @DisplayName("Некорректные данные для сохранения ItemRequest, поле пользователя не заполнено")
    public void createItemRequestUserIsEmptyTest() {
        itemRequest = ItemRequest.builder()
                .description("itemRequestDescription").build();
        assertThatExceptionOfType(DataIntegrityViolationException.class)
                .isThrownBy(() -> itemRequestRepository.save(itemRequest))
                .withMessageContaining("constraint [null]")
                .withMessageContaining("could not execute statement");
    }

    @Test
    @DisplayName("Проверяем пагинацию и работу метода getAllItemRequest")
    public void getAllItemRequestTest() {
        userRepository.save(toUser(userDto1));
        userRepository.save(toUser(new UserDto().toBuilder()
                .name("name2")
                .email("name@name.ru").build()));
        itemRequestService.createItemRequest(1L, itemRequestDto1);
        itemRequestService.createItemRequest(1L, new ItemRequestDto().toBuilder()
                .description("description2")
                .build());
        itemRequestService.createItemRequest(1L, new ItemRequestDto().toBuilder()
                .description("description3")
                .build());
        itemService.createItem(1L, ItemDto.builder()
                .id(1L)
                .name("item1")
                .description("description1")
                .available(true)
                .requestId(2L)
                .build());
        assertEquals(itemRequestService.getAllItemRequest(2L, 0, 1).size(), 1);
        assertEquals(itemRequestService.getAllItemRequest(2L, 0, 2).size(), 2);
        assertEquals(itemRequestService.getAllItemRequest(2L, 0, 3).size(), 3);
        assertEquals(itemRequestService.getAllItemRequest(2L, 3, 1).size(), 0);
        assertEquals(itemRequestService.getAllItemRequest(2L, 3, 2).size(), 1);
        assertEquals(itemRequestService.getAllItemRequest(2L, 3, 3).size(), 0);
    }

    @Test
    @DisplayName("Проверяем пагинацию и работу метода getAllByRequester")
    public void getAllByRequesterTest() {
        userRepository.save(toUser(userDto1));
        userRepository.save(toUser(new UserDto().toBuilder()
                .name("name2")
                .email("name@name.ru").build()));
        itemRequestService.createItemRequest(1L, itemRequestDto1);
        itemRequestService.createItemRequest(1L, new ItemRequestDto().toBuilder()
                .description("description2")
                .build());
        itemRequestService.createItemRequest(1L, new ItemRequestDto().toBuilder()
                .description("description3")
                .build());
        itemService.createItem(1L, ItemDto.builder()
                .id(1L)
                .name("item1")
                .description("description1")
                .available(true)
                .requestId(2L)
                .build());
        assertEquals(itemRequestService.getAllByRequester(1L, 0, 1).size(), 1);
        assertEquals(itemRequestService.getAllByRequester(1L, 0, 2).size(), 2);
        assertEquals(itemRequestService.getAllByRequester(1L, 3, 1).size(), 0);
        assertEquals(itemRequestService.getAllByRequester(1L, 3, 2).size(), 1);
        assertEquals(itemRequestService.getAllByRequester(1L, 3, 3).size(), 0);
        assertEquals(itemRequestService.getAllByRequester(2L, 0, 3).size(), 0);
        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> itemRequestService.getAllByRequester(3L, 0, 3))
                .withMessage("Пользователь с id = 3  не найден");
    }


    @Test
    @DisplayName("Проверяем работу метода getByIdItemRequest")
    public void getByIdItemRequestTest() {
        userRepository.save(toUser(userDto1));
        itemRequestService.createItemRequest(1L, itemRequestDto1);
        ItemRequestResponseDto itemRequestResponseDto2 = itemRequestService.createItemRequest(1L, new ItemRequestDto().toBuilder()
                .description("description2")
                .build());
        itemService.createItem(1L, ItemDto.builder()
                .id(1L)
                .name("item1")
                .description("description1")
                .available(true)
                .requestId(2L)
                .build());

        ItemRequestResponseDto itemRequestResponseDto = itemRequestService.getByIdItemRequest(1L, 2L);
        assertThat(itemRequestResponseDto)
                .isNotNull()
                .extracting(ItemRequestResponseDto::getDescription)
                .isEqualTo(itemRequestResponseDto2.getDescription());
    }


}
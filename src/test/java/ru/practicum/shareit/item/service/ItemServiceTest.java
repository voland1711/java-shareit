package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.exception.BadRequestException;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.exception.ValidationException;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.item.model.ItemMapper.toItem;
import static ru.practicum.shareit.item.model.ItemMapper.toItemDto;
import static ru.practicum.shareit.user.model.UserMapper.toUser;

@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemServiceTest {
    private final UserRepository userRepository;
    private final ItemRequestService itemRequestService;
    private final ItemServiceImpl itemService;
    private ItemDto itemDto1;
    private ItemDto itemDto2;
    private ItemDto itemDto3;
    private Item item1;
    private Item item2;
    private UserDto userDto1;
    private UserDto userDto2;


    @BeforeEach
    void setup() {
        itemDto1 = ItemDto.builder()
                .id(1L)
                .name("item1")
                .description("description1")
                .available(true)
                .build();
        itemDto2 = ItemDto.builder()
                .name("item2")
                .description("description2")
                .available(true)
                .build();
        itemDto3 = ItemDto.builder()
                .build();
        item1 = Item.builder()
                .id(1L)
                .name("item1")
                .description("description1")
                .available(true)
                .build();
        item2 = Item.builder()
                .id(2L)
                .name("item2")
                .description("description2")
                .available(true)
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
    @DisplayName("Отсуствует значение поля available")
    public void createItemAvailableIsMissingTest() {
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> itemService.createItem(1L, itemDto3))
                .withMessage("Отсутсвует параметр available");
    }

    @Test
    @DisplayName("Имя вещи пустое/содержит пробелы")
    public void createItemNameIsEmptyTest() {
        itemDto3.setAvailable(true);
        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> itemService.createItem(1L, itemDto3))
                .withMessage("Имя вещи пустое/содержит пробелы");
    }

    @Test
    @DisplayName("Описание вещи пустое/содержит пробелы")
    public void createItemDescriptionIsEmptyTest() {
        itemDto3.setAvailable(true);
        itemDto3.setName("name3");
        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> itemService.createItem(1L, itemDto3))
                .withMessage("Описание вещи пустое/содержит пробелы");
    }

    @Test
    @DisplayName("Пользователь с id = 1  не найден")
    public void createItemUserNotFoundTest() {
        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> itemService.createItem(1L, itemDto2))
                .withMessage("Пользователь с id = 1  не найден");
    }

    @Test
    @DisplayName("Создание item1, все поля заполнены соответствующими данными")
    public void createItemTest() {
        userRepository.save(toUser(userDto1));
        ItemDto itemDto = itemService.createItem(1L, itemDto1);
        assertEquals(itemDto, toItemDto(item1));
        assertEquals(toItem(itemDto), item1);
    }

    @Test
    @DisplayName("Поле requestId, указывает на несуществующий объект")
    public void createItemRequestIdNotFoundTest() {
        itemDto1.setRequestId(1L);
        userRepository.save(toUser(userDto1));

        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> itemService.createItem(1L, itemDto1))
                .withMessage("Запрос с id = 1 не найден");
    }

    @Test
    @DisplayName("Поле requestId указан верно")
    public void createItemWithRequestIdTest() {
        itemDto1.setRequestId(1L);
        userRepository.save(toUser(userDto1));
        itemRequestService.createItemRequest(1L,
                ItemRequestDto.builder()
                        .description("itemRequestDescription1")
                        .build());
        ItemDto itemDto = itemService.createItem(1L, itemDto1);
        assertEquals(itemDto1, itemDto);
    }


    @Test
    @DisplayName("Запрос выполнен с корректными данными")
    public void getItemByIdTest() {
        userRepository.save(toUser(userDto1));
        itemService.createItem(1L, itemDto1);
        ItemDto itemDto = itemService.getItemById(1L, 1l);
        itemDto1.setComments(new ArrayList<>());
        assertEquals(itemDto1, itemDto);
    }

    @Test
    @DisplayName("Объект item c id = 1, отсутствует")
    public void getItemByIdNotFoundTest() {
        userRepository.save(toUser(userDto1));
        assertThatExceptionOfType(ItemNotFoundException.class)
                .isThrownBy(() -> itemService.getItemById(1L, 1l))
                .withMessage("Вещь id = 1 в коллекции не найдена");
    }

    @Test
    @DisplayName("Обновление item c id = 1, данные корректны")
    public void updateItemTest() {
        userRepository.save(toUser(userDto1));
        ItemDto itemDto = itemService.createItem(1L, itemDto1);
        assertEquals(itemDto, toItemDto(item1));
        ItemDto itemDtoUpdate = itemService.updateItem(1L, itemDto2, 1L);
        itemDto.setName("item2");
        itemDto.setDescription("description2");
        assertEquals(itemDtoUpdate, itemDto);
    }

    @Test
    @DisplayName("Обновление, item отсуствует")
    public void updateItemNotFoundTest() {
        userRepository.save(toUser(userDto1));
        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> itemService.updateItem(1L, itemDto1, 1L))
                .withMessage("Вещь с id = 1 не найдена");
    }

    @Test
    @DisplayName("Обновление item: владелец другой")
    public void updateItemOwnerOtherTest() {
        userRepository.save(toUser(userDto1));
        userRepository.save(toUser(userDto2));
        itemService.createItem(1L, itemDto1);
        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> itemService.updateItem(2L, itemDto1, 1L))
                .withMessage("Обновить данные вещи может только ее владелец");
    }

    @Test
    @DisplayName("Запрос выполнен с корректными данными")
    public void getAllItemsTest() {
        userRepository.save(toUser(userDto1));
        assertEquals(itemService.getAllItems(1L).size(), 0);
        itemService.createItem(1L, itemDto1);
        userRepository.save(toUser(userDto2));
        itemService.createItem(1L, itemDto2);
        List<ItemDto> itemDtoList = itemService.getAllItems(1L);
        assertEquals(itemService.getAllItems(1L).size(), 2);

        assertEquals(new HashSet<>(itemDtoList.stream()
                .map(ItemMapper::toItem)
                .collect(Collectors.toList())), new HashSet<>(Arrays.asList(item1, item2)));
    }

    @Test
    @DisplayName("Проверка работы метода searchItems")
    public void searchItemsTest() {
        assertEquals(itemService.searchItems(1L, "").size(), 0);

        userRepository.save(toUser(userDto1));
        itemService.createItem(1L, itemDto1);
        itemService.createItem(1L, itemDto2);
        assertEquals(itemService.searchItems(1L, "Escription2").size(), 1);
        assertEquals(itemService.searchItems(2L, "desc").size(), 2);

    }
}

package ru.practicum.shareit.shareit.item.service;

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
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.ObjectNotFoundException;
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
    private ItemDto firstItemDto;
    private ItemDto secondItemDto;
    private ItemDto threeItemDto;
    private Item firstItem;
    private Item secondItem;
    private UserDto firstUserDto;
    private UserDto secondUserDto;


    @BeforeEach
    void setup() {
        firstItemDto = createFirstItemDto();
        secondItemDto = createSecondItemDto();
        threeItemDto = ItemDto.builder()
                .build();
        firstItem = createFirstItem();
        secondItem = createSecondItem();
        firstUserDto = createFirstUserDto();
        secondUserDto = createSecondUserDto();
    }

    private ItemDto createFirstItemDto() {
        return ItemDto.builder()
                .id(1L)
                .name("firstItem")
                .description("description1")
                .available(true)
                .build();
    }

    private ItemDto createSecondItemDto() {
        return ItemDto.builder()
                .name("secondItem")
                .description("description2")
                .available(true)
                .build();
    }

    private Item createFirstItem() {
        return Item.builder()
                .id(1L)
                .name("firstItem")
                .description("description1")
                .available(true)
                .build();
    }

    private Item createSecondItem() {
        return Item.builder()
                .id(2L)
                .name("secondItem")
                .description("description2")
                .available(true)
                .build();
    }

    private UserDto createFirstUserDto() {
        return UserDto.builder()
                .name("nameFirstUser")
                .email("nameFirstUser@user.ru")
                .build();
    }

    private UserDto createSecondUserDto() {
        return UserDto.builder()
                .name("nameSecondUser")
                .email("nameSecondUser@user.ru")
                .build();
    }

    @Test
    @DisplayName("Пользователь с id = 1  не найден")
    public void createItemUserNotFoundTest() {
        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> itemService.createItem(1L, secondItemDto))
                .withMessage("Пользователь с id = 1  не найден");
    }

    @Test
    @DisplayName("Создание firstItem, все поля заполнены соответствующими данными")
    public void createItemTest() {
        userRepository.save(toUser(firstUserDto));
        ItemDto itemDto = itemService.createItem(1L, firstItemDto);
        assertEquals(itemDto, toItemDto(firstItem));
        assertEquals(toItem(itemDto), firstItem);
    }

    @Test
    @DisplayName("Поле requestId, указывает на несуществующий объект")
    public void createItemRequestIdNotFoundTest() {
        firstItemDto.setRequestId(1L);
        userRepository.save(toUser(firstUserDto));

        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> itemService.createItem(1L, firstItemDto))
                .withMessage("Запрос с id = 1 не найден");
    }

    @Test
    @DisplayName("Поле requestId указан верно")
    public void createItemWithRequestIdTest() {
        firstItemDto.setRequestId(1L);
        userRepository.save(toUser(firstUserDto));
        itemRequestService.createItemRequest(1L,
                ItemRequestDto.builder()
                        .description("itemRequestDescription1")
                        .build());
        ItemDto itemDto = itemService.createItem(1L, firstItemDto);
        assertEquals(firstItemDto, itemDto);
    }

    @Test
    @DisplayName("Запрос выполнен с корректными данными")
    public void getItemByIdTest() {
        userRepository.save(toUser(firstUserDto));
        itemService.createItem(1L, firstItemDto);
        ItemDto itemDto = itemService.getItemById(1L, 1L);
        firstItemDto.setComments(new ArrayList<>());
        assertEquals(firstItemDto, itemDto);
    }

    @Test
    @DisplayName("Объект item c id = 1, отсутствует")
    public void getItemByIdNotFoundTest() {
        userRepository.save(toUser(firstUserDto));
        assertThatExceptionOfType(ItemNotFoundException.class)
                .isThrownBy(() -> itemService.getItemById(1L, 1L))
                .withMessage("Вещь id = 1 в коллекции не найдена");
    }

    @Test
    @DisplayName("Обновление item c id = 1, данные корректны")
    public void updateItemTest() {
        userRepository.save(toUser(firstUserDto));
        ItemDto itemDto = itemService.createItem(1L, firstItemDto);
        assertEquals(itemDto, toItemDto(firstItem));
        ItemDto itemDtoUpdate = itemService.updateItem(1L, secondItemDto, 1L);
        itemDto.setName("secondItem");
        itemDto.setDescription("description2");
        assertEquals(itemDtoUpdate, itemDto);
    }

    @Test
    @DisplayName("Обновление, item отсуствует")
    public void updateItemNotFoundTest() {
        userRepository.save(toUser(firstUserDto));
        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> itemService.updateItem(1L, firstItemDto, 1L))
                .withMessage("Вещь с id = 1 не найдена");
    }

    @Test
    @DisplayName("Обновление item: владелец другой")
    public void updateItemOwnerOtherTest() {
        userRepository.save(toUser(firstUserDto));
        userRepository.save(toUser(secondUserDto));
        itemService.createItem(1L, firstItemDto);
        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> itemService.updateItem(2L, firstItemDto, 1L))
                .withMessage("Обновить данные вещи может только ее владелец");
    }

    @Test
    @DisplayName("Запрос выполнен с корректными данными")
    public void getAllItemsTest() {
        userRepository.save(toUser(firstUserDto));
        assertEquals(itemService.getAllItems(1L).size(), 0);
        itemService.createItem(1L, firstItemDto);
        userRepository.save(toUser(secondUserDto));
        itemService.createItem(1L, secondItemDto);
        List<ItemDto> itemDtoList = itemService.getAllItems(1L);
        assertEquals(itemService.getAllItems(1L).size(), 2);

        assertEquals(new HashSet<>(itemDtoList.stream()
                .map(ItemMapper::toItem)
                .collect(Collectors.toList())), new HashSet<>(Arrays.asList(firstItem, secondItem)));
    }

    @Test
    @DisplayName("Проверка работы метода searchItems")
    public void searchItemsTest() {
        assertEquals(itemService.searchItems(1L, "").size(), 0);

        userRepository.save(toUser(firstUserDto));
        itemService.createItem(1L, firstItemDto);
        itemService.createItem(1L, secondItemDto);
        assertEquals(itemService.searchItems(1L, "Escription2").size(), 1);
        assertEquals(itemService.searchItems(2L, "desc").size(), 2);
    }
}

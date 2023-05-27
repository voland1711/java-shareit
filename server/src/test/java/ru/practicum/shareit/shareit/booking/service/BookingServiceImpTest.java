package ru.practicum.shareit.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.exception.BadRequestException;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImp;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static ru.practicum.shareit.booking.model.BookingState.*;
import static ru.practicum.shareit.booking.model.BookingStatus.REJECTED;
import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;
import static ru.practicum.shareit.booking.model.BookingStatus.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingServiceImpTest {
    private final BookingServiceImp bookingService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private User firstUser;
    private User secondUser;
    private Item item;
    private BookingDto bookingDto1;
    private BookingShortDto bookingShortDto;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private LocalDateTime start = LocalDateTime.parse(LocalDateTime.now().plusMinutes(2).format(dateTimeFormatter));
    private LocalDateTime end = LocalDateTime.parse(LocalDateTime.now().plusMinutes(10).format(dateTimeFormatter));
    @Autowired
    private BookingRepository bookingRepository;

    @BeforeEach
    void setup() {
        firstUser = createFirstUser();
        secondUser = createSecondUser();
        item = createItem();
        bookingDto1 = createBookingDto();
        bookingShortDto = createBookingShortDto();
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

    private Item createItem() {
        return Item.builder()
                .id(1L)
                .name("nameFirstItem")
                .description("descriptionFirstItem")
                .owner(secondUser)
                .available(true)
                .build();
    }

    private BookingDto createBookingDto() {
        return BookingDto.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(firstUser)
                .status(BookingStatus.WAITING)
                .build();
    }

    private BookingShortDto createBookingShortDto() {
        return BookingShortDto.builder()
                .itemId(1L)
                .start(start)
                .end(end)
                .build();
    }

    @Test
    public void createBookingTest() {
        userRepository.save(firstUser);
        userRepository.save(secondUser);
        itemRepository.save(item);
        BookingDto result = bookingService.createBooking(bookingShortDto, 1L);
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(firstUser.getId(), result.getBooker().getId());
        assertEquals(item.getId(), result.getItem().getId());
        assertEquals(WAITING, result.getStatus());
    }


    @Test
    public void createBookingUserNotFoundTest() {
        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> bookingService.createBooking(bookingShortDto, 1L))
                .withMessage("Пользователь с id: 1 не найден");
    }

    @Test
    public void createBookingUserItemNotFoundTest() {
        userRepository.save(firstUser);
        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> bookingService.createBooking(bookingShortDto, 1L))
                .withMessage("Вещь с id = 1 не найдена");
    }


    @Test
    public void createBookingUserItemIdEmptyTest() {
        userRepository.save(firstUser);
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> bookingService.createBooking(new BookingShortDto().toBuilder()
                        .start(start)
                        .end(end)
                        .build(), 1L));
    }

    @Test
    public void createBookingAvailableTrueTest() {
        userRepository.save(firstUser);
        userRepository.save(secondUser);
        item.setAvailable(false);
        itemRepository.save(item);
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> bookingService.createBooking(bookingShortDto, 1L))
                .withMessage("Вещь уже забронирована");
    }

    @Test
    public void createBookingUserEqualsOwnerTest() {
        userRepository.save(firstUser);
        userRepository.save(secondUser);
        itemRepository.save(item);
        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> bookingService.createBooking(bookingShortDto, 2L))
                .withMessage("Владелец не может забронировать вещь");
    }

    @Test
    public void createBookingEndTimeInPastTest() {
        userRepository.save(firstUser);
        userRepository.save(secondUser);
        itemRepository.save(item);
        bookingShortDto.setEnd(start.minusMonths(2));
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> bookingService.createBooking(bookingShortDto, 1L))
                .withMessage("Ошибка бронирования: дата окончания ранее даты начала бронирования");
    }

    @Test
    public void approveBookingBookingNotFoundTest() {
        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> bookingService.approveBooking(1L, 1L, false))
                .withMessage("Бронирование с id = 1 не найдено");
    }

    @Test
    public void approveBookingUserNotEqualsOwnerTest() {
        userRepository.save(firstUser);
        userRepository.save(secondUser);
        itemRepository.save(item);
        bookingService.createBooking(bookingShortDto, 1L);
        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> bookingService.approveBooking(1L, 1L, false))
                .withMessage("Подтвердить бронирование может только владелец вещи");
    }

    @Test
    public void approveBookingStatusRejectedTest() {
        userRepository.save(firstUser);
        userRepository.save(secondUser);
        itemRepository.save(item);
        bookingService.createBooking(bookingShortDto, 1L);
        bookingService.approveBooking(2L, 1L, false);
        BookingDto result = bookingService.getById(1L, 1L);
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(firstUser.getId(), result.getBooker().getId());
        assertEquals(item.getId(), result.getItem().getId());
        assertEquals(REJECTED, result.getStatus());
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> bookingService.approveBooking(2L, 1L, false))
                .withMessage("Произошла ошибка бронирования");
    }

    @Test
    public void approveBookingStatusTrueTest() {
        userRepository.save(firstUser);
        userRepository.save(secondUser);
        itemRepository.save(item);
        bookingService.createBooking(bookingShortDto, 1L);
        bookingService.approveBooking(2L, 1L, true);
        BookingDto result = bookingService.getById(1L, 1L);
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(firstUser.getId(), result.getBooker().getId());
        assertEquals(item.getId(), result.getItem().getId());
        assertEquals(APPROVED, result.getStatus());
    }

    @Test
    public void getAllByOwnerStateAllTest() {
        //user not found
        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> bookingService.getAllByOwner(1L, ALL, 1, 1))
                .withMessage("Пользователь с id: 1 не найден");
        userRepository.save(firstUser);
        List<BookingDto> bookingDtoList = bookingService.getAllByOwner(1L, ALL, 1, 1);
        assertEquals(bookingDtoList.size(), 0);
        userRepository.save(secondUser);
        itemRepository.save(item);
        bookingService.createBooking(bookingShortDto, 1L);
        bookingDtoList = bookingService.getAllByOwner(2L, ALL, 0, 1);
        assertEquals(bookingDtoList.size(), 1);
    }

    @Test
    public void getAllByOwnerStateCurrentTest() {
        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> bookingService.getAllByOwner(1L, CURRENT, 1, 1))
                .withMessage("Пользователь с id: 1 не найден");
        userRepository.save(firstUser);
        List<BookingDto> bookingDtoList = bookingService.getAllByOwner(1L, CURRENT, 1, 1);
        assertEquals(bookingDtoList.size(), 0);
        userRepository.save(secondUser);
        itemRepository.save(item);
        bookingShortDto.setStart(start.minusDays(2));
        bookingService.createBooking(bookingShortDto, 1L);
        bookingDtoList = bookingService.getAllByOwner(2L, CURRENT, 0, 1);
        assertEquals(bookingDtoList.size(), 1);
    }

    @Test
    public void getAllByOwnerStatePastTest() {
        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> bookingService.getAllByOwner(1L, PAST, 1, 1))
                .withMessage("Пользователь с id: 1 не найден");
        userRepository.save(firstUser);
        List<BookingDto> bookingDtoList = bookingService.getAllByOwner(1L, PAST, 1, 1);
        assertEquals(bookingDtoList.size(), 0);
        userRepository.save(secondUser);
        itemRepository.save(item);
        bookingShortDto.setEnd(start.minusDays(2));
        bookingShortDto.setStart(start.minusDays(5));
        bookingService.createBooking(bookingShortDto, 1L);
        bookingDtoList = bookingService.getAllByOwner(2L, PAST, 0, 1);
        assertEquals(bookingDtoList.size(), 1);
    }

    @Test
    public void getAllByOwnerStateFutureTest() {
        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> bookingService.getAllByOwner(1L, FUTURE, 1, 1))
                .withMessage("Пользователь с id: 1 не найден");
        userRepository.save(firstUser);
        List<BookingDto> bookingDtoList = bookingService.getAllByOwner(1L, FUTURE, 1, 1);
        assertEquals(bookingDtoList.size(), 0);
        userRepository.save(secondUser);
        itemRepository.save(item);
        bookingShortDto.setStart(start.plusDays(1));
        bookingShortDto.setEnd(start.plusDays(5));
        bookingService.createBooking(bookingShortDto, 1L);
        bookingDtoList = bookingService.getAllByOwner(2L, FUTURE, 0, 1);
        assertEquals(bookingDtoList.size(), 1);
    }

    @Test
    public void getAllByOwnerStateWaitingTest() {
        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> bookingService.getAllByOwner(1L, BookingState.WAITING, 1, 1))
                .withMessage("Пользователь с id: 1 не найден");
        userRepository.save(firstUser);
        List<BookingDto> bookingDtoList = bookingService.getAllByOwner(1L, BookingState.WAITING, 1, 1);
        assertEquals(bookingDtoList.size(), 0);
        userRepository.save(secondUser);
        itemRepository.save(item);
        bookingService.createBooking(bookingShortDto, 1L);
        bookingDtoList = bookingService.getAllByOwner(2L, BookingState.WAITING, 0, 1);
        assertEquals(bookingDtoList.size(), 1);
    }

    @Test
    public void getAllByOwnerStateRejectedTest() {
        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> bookingService.getAllByOwner(2L, BookingState.REJECTED, 1, 1))
                .withMessage("Пользователь с id: 2 не найден");
        userRepository.save(firstUser);
        userRepository.save(secondUser);
        List<BookingDto> bookingDtoList = bookingService.getAllByOwner(2L, BookingState.REJECTED, 1, 1);
        assertEquals(bookingDtoList.size(), 0);
        itemRepository.save(item);
        bookingService.createBooking(bookingShortDto, 1L);
        bookingService.approveBooking(2L, 1L, false);
        bookingDtoList = bookingService.getAllByOwner(2L, BookingState.REJECTED, 0, 1);
        assertEquals(bookingDtoList.size(), 1);
    }

    @Test
    public void getAllByUserStateAllTest() {
        //user not found
        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> bookingService.getAllByUser(1L, ALL, 1, 1))
                .withMessage("Пользователь с id: 1 не найден");
        userRepository.save(firstUser);
        List<BookingDto> bookingDtoList = bookingService.getAllByUser(1L, ALL, 1, 1);
        assertEquals(bookingDtoList.size(), 0);
        userRepository.save(secondUser);
        itemRepository.save(item);
        bookingService.createBooking(bookingShortDto, 1L);
        bookingDtoList = bookingService.getAllByUser(1L, ALL, 0, 1);
        assertEquals(bookingDtoList.size(), 1);
    }

    @Test
    public void getAllByUserStateCurrentTest() {
        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> bookingService.getAllByUser(1L, CURRENT, 1, 1))
                .withMessage("Пользователь с id: 1 не найден");
        userRepository.save(firstUser);
        List<BookingDto> bookingDtoList = bookingService.getAllByUser(1L, CURRENT, 1, 1);
        assertEquals(bookingDtoList.size(), 0);
        userRepository.save(secondUser);
        itemRepository.save(item);
        bookingShortDto.setStart(start.minusDays(2));
        bookingService.createBooking(bookingShortDto, 1L);
        bookingDtoList = bookingService.getAllByUser(1L, CURRENT, 0, 1);
        assertEquals(bookingDtoList.size(), 1);
    }

    @Test
    public void getAllByUserStatePastTest() {
        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> bookingService.getAllByUser(1L, PAST, 1, 1))
                .withMessage("Пользователь с id: 1 не найден");
        userRepository.save(firstUser);
        List<BookingDto> bookingDtoList = bookingService.getAllByUser(1L, PAST, 1, 1);
        assertEquals(bookingDtoList.size(), 0);
        userRepository.save(secondUser);
        itemRepository.save(item);
        bookingShortDto.setEnd(start.minusDays(2));
        bookingShortDto.setStart(start.minusDays(5));
        bookingService.createBooking(bookingShortDto, 1L);
        bookingDtoList = bookingService.getAllByUser(1L, PAST, 0, 1);
        assertEquals(bookingDtoList.size(), 1);
    }

    @Test
    public void getAllByUserStateFutureTest() {
        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> bookingService.getAllByUser(1L, FUTURE, 1, 1))
                .withMessage("Пользователь с id: 1 не найден");
        userRepository.save(firstUser);
        List<BookingDto> bookingDtoList = bookingService.getAllByUser(1L, FUTURE, 1, 1);
        assertEquals(bookingDtoList.size(), 0);
        userRepository.save(secondUser);
        itemRepository.save(item);
        bookingShortDto.setStart(start.plusDays(1));
        bookingShortDto.setEnd(start.plusDays(5));
        bookingService.createBooking(bookingShortDto, 1L);
        bookingDtoList = bookingService.getAllByUser(1L, FUTURE, 0, 1);
        assertEquals(bookingDtoList.size(), 1);
    }

    @Test
    public void getAllByUserStateWaitingTest() {
        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> bookingService.getAllByUser(1L, BookingState.WAITING, 1, 1))
                .withMessage("Пользователь с id: 1 не найден");
        userRepository.save(firstUser);
        List<BookingDto> bookingDtoList = bookingService.getAllByUser(1L, BookingState.WAITING, 1, 1);
        assertEquals(bookingDtoList.size(), 0);
        userRepository.save(secondUser);
        itemRepository.save(item);
        bookingService.createBooking(bookingShortDto, 1L);
        bookingDtoList = bookingService.getAllByUser(1L, BookingState.WAITING, 0, 1);
        assertEquals(bookingDtoList.size(), 1);
    }

    @Test
    public void getAllByUserStateRejectedTest() {
        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> bookingService.getAllByUser(1L, BookingState.REJECTED, 1, 1))
                .withMessage("Пользователь с id: 1 не найден");
        userRepository.save(firstUser);
        List<BookingDto> bookingDtoList = bookingService.getAllByUser(1L, BookingState.REJECTED, 1, 1);
        assertEquals(bookingDtoList.size(), 0);
        userRepository.save(secondUser);
        itemRepository.save(item);
        bookingService.createBooking(bookingShortDto, 1L);
        bookingService.approveBooking(2L, 1L, false);
        bookingDtoList = bookingService.getAllByUser(1L, BookingState.REJECTED, 0, 1);
        assertEquals(bookingDtoList.size(), 1);
    }

}


package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureMockMvc
public class BookingServiceImpTest {
    private final BookingServiceImp bookingService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private User user1;
    private User user2;
    private Item item1;
    private BookingDto bookingDto1;
    private BookingShortDto bookingShortDto;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private LocalDateTime start = LocalDateTime.parse(LocalDateTime.now().plusMinutes(2).format(dateTimeFormatter));
    private LocalDateTime end = LocalDateTime.parse(LocalDateTime.now().plusMinutes(10).format(dateTimeFormatter));

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

        item1 = Item.builder()
                .id(1L)
                .name("nameItem1")
                .description("descriptionItem1")
                .owner(user2)
                .available(true)
                .build();

        bookingDto1 = BookingDto.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item1)
                .booker(user1)
                .status(BookingStatus.WAITING)
                .build();

        bookingShortDto = BookingShortDto.builder()
                .itemId(1L)
                .start(start)
                .end(end)
                .build();
    }


    @Test
    public void createBooking() {
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        BookingDto result = bookingService.createBooking(bookingShortDto, 1L);
        // Проверка результата
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(user1.getId(), result.getBooker().getId());
        assertEquals(item1.getId(), result.getItem().getId());
        assertEquals(WAITING, result.getStatus());
    }


    @Test
    public void createBookingErrorNameNull() {

    }
}


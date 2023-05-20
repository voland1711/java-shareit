package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.exception.BadRequestException;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest({BookingController.class})
class BookingControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private LocalDateTime start = LocalDateTime.parse(LocalDateTime.now().plusMinutes(2).format(dateTimeFormatter));
    private LocalDateTime end = LocalDateTime.parse(LocalDateTime.now().plusMinutes(10).format(dateTimeFormatter));
    private User user;
    private User owner;
    private Item item;
    private BookingDto bookingDto;
    private BookingShortDto bookingShortDto;
    private Long userIdNotFoud = 101L;


    @BeforeEach
    void setup() {
        user = createUser();
        owner = createOwner();
        item = createItem();
        bookingDto = createBookingDto();
        bookingShortDto = createBookingShortDto();
    }

    private User createUser() {
        return User.builder()
                .id(1L)
                .name("nameFirstUser")
                .email("nameFirstUser@user.ru")
                .build();
    }

    private User createOwner() {
        return User.builder()
                .id(2L)
                .name("nameSecondUser")
                .email("nameSecondUser@user.ru")
                .build();
    }

    private Item createItem() {
        return Item.builder()
                .id(3L)
                .name("nameFirstItem")
                .description("descriptionFirstItem")
                .owner(owner)
                .available(true)
                .build();
    }

    private BookingDto createBookingDto() {
        return BookingDto.builder()
                .id(4L)
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();
    }

    private BookingShortDto createBookingShortDto() {
        return BookingShortDto.builder()
                .itemId(3L)
                .start(start)
                .end(end)
                .build();
    }

    @SneakyThrows
    @Test
    public void getAllByUserNotFoundTest() {
        when(bookingService.getAllByUser(userIdNotFoud, BookingState.ALL, 0, 10))
                .thenThrow(new ObjectNotFoundException("User with id: " + userIdNotFoud + " not found"));

        MvcResult mvcResult = this.mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userIdNotFoud))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        assertTrue(responseBody.contains("User with id: " + userIdNotFoud + " not found"));
    }

    @SneakyThrows
    @Test
    public void getAllByUserTest() {
        when(bookingService.getAllByUser(1L, BookingState.ALL, 0, 20))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(4L), Long.class))
                .andExpect(jsonPath("$[0].status", is("WAITING")))
                .andExpect(jsonPath("$[0].item.name", is("nameFirstItem")))
                .andExpect(jsonPath("$[0].booker.name", is("nameFirstUser")));
    }

    @Test
    public void isNotExistEndPoint() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/non-existent-url"))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getAllByOwner() {
        when(bookingService.getAllByOwner(1L, BookingState.UNSUPPORTED_STATUS, 0, 10))
                .thenThrow(new BadRequestException("Unknown state: UNSUPPORTED_STATUS"));
        MvcResult mvcResult = this.mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "UNSUPPORTED_STATUS"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        assertTrue(responseBody.contains("Unknown state: UNSUPPORTED_STATUS"));
    }

    @SneakyThrows
    @Test
    void getByIdTest() {
        when(bookingService.getById(any(), any()))
                .thenReturn(bookingDto);

        String resultResponse = mockMvc.perform(get("/bookings/{bookingId}", 4L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.status").value(bookingDto.getStatus().toString()))
                .andExpect(jsonPath("$.item.name").value(bookingDto.getItem().getName()))
                .andExpect(jsonPath("$.booker.name").value(bookingDto.getBooker().getName()))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDto), resultResponse);
    }

    @SneakyThrows
    @Test
    void createBooking() {
        when(bookingService.createBooking(eq(bookingShortDto), anyLong()))
                .thenReturn(bookingDto);

        String resultResponse = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingShortDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.status").value(bookingDto.getStatus().toString()))
                .andExpect(jsonPath("$.item.name").value(bookingDto.getItem().getName()))
                .andExpect(jsonPath("$.booker.name").value(bookingDto.getBooker().getName()))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDto), resultResponse);
    }

    @SneakyThrows
    @Test
    void approve() {
        bookingDto.setStatus(BookingStatus.APPROVED);
        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDto);

        String resultResponse = mockMvc.perform(patch("/bookings/{bookingId}", 4L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.status").value(bookingDto.getStatus().toString()))
                .andExpect(jsonPath("$.item.name").value(bookingDto.getItem().getName()))
                .andExpect(jsonPath("$.booker.name").value(bookingDto.getBooker().getName()))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDto), resultResponse);
    }
}
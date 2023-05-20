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
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private LocalDateTime start = LocalDateTime.parse(LocalDateTime.now().plusMinutes(2).format(dateTimeFormatter));
    private LocalDateTime end = LocalDateTime.parse(LocalDateTime.now().plusMinutes(10).format(dateTimeFormatter));
    private User user1;
    private User owner1;
    private Item item1;
    private BookingDto bookingDto1;
    private BookingShortDto bookingShortDto;
    private Long userIdNotFoud = 101L;


    @BeforeEach
    void setup() {
        user1 = User.builder()
                .id(1L)
                .name("nameUser1")
                .email("nameuser1@user.ru")
                .build();

        owner1 = User.builder()
                .id(2L)
                .name("nameUser2")
                .email("nameuser2@user.ru")
                .build();

        item1 = Item.builder()
                .id(3L)
                .name("nameItem1")
                .description("descriptionItem1")
                .owner(owner1)
                .available(true)
                .build();

        bookingDto1 = BookingDto.builder()
                .id(4L)
                .start(start)
                .end(end)
                .item(item1)
                .booker(user1)
                .status(BookingStatus.WAITING)
                .build();

        bookingShortDto = BookingShortDto.builder()
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
                .thenReturn(List.of(bookingDto1));

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
                .andExpect(jsonPath("$[0].item.name", is("nameItem1")))
                .andExpect(jsonPath("$[0].booker.name", is("nameUser1")));
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
                .thenReturn(bookingDto1);

        String resultResponse = mockMvc.perform(get("/bookings/{bookingId}", 4L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto1.getId()))
                .andExpect(jsonPath("$.status").value(bookingDto1.getStatus().toString()))
                .andExpect(jsonPath("$.item.name").value(bookingDto1.getItem().getName()))
                .andExpect(jsonPath("$.booker.name").value(bookingDto1.getBooker().getName()))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDto1), resultResponse);
    }

    @SneakyThrows
    @Test
    void createBooking() {
        when(bookingService.createBooking(eq(bookingShortDto), anyLong()))
                .thenReturn(bookingDto1);

        String resultResponse = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingShortDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto1.getId()))
                .andExpect(jsonPath("$.status").value(bookingDto1.getStatus().toString()))
                .andExpect(jsonPath("$.item.name").value(bookingDto1.getItem().getName()))
                .andExpect(jsonPath("$.booker.name").value(bookingDto1.getBooker().getName()))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDto1), resultResponse);
    }

    @SneakyThrows
    @Test
    void approve() {
        bookingDto1.setStatus(BookingStatus.APPROVED);
        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDto1);

        String resultResponse = mockMvc.perform(patch("/bookings/{bookingId}", 4L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto1.getId()))
                .andExpect(jsonPath("$.status").value(bookingDto1.getStatus().toString()))
                .andExpect(jsonPath("$.item.name").value(bookingDto1.getItem().getName()))
                .andExpect(jsonPath("$.booker.name").value(bookingDto1.getBooker().getName()))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDto1), resultResponse);
    }
}
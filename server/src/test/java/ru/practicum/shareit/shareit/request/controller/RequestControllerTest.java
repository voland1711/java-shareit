package ru.practicum.shareit.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.conroller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ItemRequestController.class)
public class RequestControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean

    private ItemRequestService itemRequestService;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private LocalDateTime createdLocalDateTime = LocalDateTime.parse(LocalDateTime.now().minusDays(3).format(dateTimeFormatter));

    @Test
    @SneakyThrows
    public void createItemRequestTest() {
        ItemRequestResponseDto itemRequestResponseDto = ItemRequestResponseDto.builder()
                .id(1L)
                .description("Description")
                .created(createdLocalDateTime)
                .items(new ArrayList<>())
                .build();

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("Description").build();

        when(itemRequestService.createItemRequest(any(), any()))
                .thenReturn(itemRequestResponseDto);

        String resultResponse = mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestResponseDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestResponseDto.getDescription()))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestResponseDto), resultResponse);
    }

    @Test
    @SneakyThrows
    public void getByIdTest() {
        ItemRequestResponseDto itemRequestResponseDto = ItemRequestResponseDto.builder()
                .id(1L)
                .description("Description")
                .created(createdLocalDateTime)
                .items(new ArrayList<>())
                .build();

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("Description").build();

        when(itemRequestService.getByIdItemRequest(any(), any()))
                .thenReturn(itemRequestResponseDto);

        String resultResponse = mockMvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestResponseDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestResponseDto.getDescription()))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(itemRequestResponseDto), resultResponse);
    }

    @Test
    @SneakyThrows
    public void getAllItemRequestTest() {
        ItemRequestResponseDto itemRequestResponseDto = ItemRequestResponseDto.builder()
                .id(1L)
                .description("Description")
                .created(createdLocalDateTime)
                .items(new ArrayList<>())
                .build();

        when(itemRequestService.getAllItemRequest(any(), any(), any()))
                .thenReturn(List.of(itemRequestResponseDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is((itemRequestResponseDto.getDescription()))))
                .andExpect(jsonPath("$[0].created", is((itemRequestResponseDto.getCreated().format(dateTimeFormatter)))))
                .andExpect(jsonPath("$[0].items", is((itemRequestResponseDto.getItems()))));
    }

    @Test
    @SneakyThrows
    public void getAllByRequesterTest() {
        ItemRequestResponseDto itemRequestResponseDto = ItemRequestResponseDto.builder()
                .id(1L)
                .description("Description")
                .created(createdLocalDateTime)
                .items(new ArrayList<>())
                .build();

        when(itemRequestService.getAllByRequester(any(), any(), any()))
                .thenReturn(List.of(itemRequestResponseDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is((itemRequestResponseDto.getDescription()))))
                .andExpect(jsonPath("$[0].created", is((itemRequestResponseDto.getCreated().format(dateTimeFormatter)))))
                .andExpect(jsonPath("$[0].items", is((itemRequestResponseDto.getItems()))));
    }
}

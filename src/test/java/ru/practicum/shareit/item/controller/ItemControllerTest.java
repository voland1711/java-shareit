package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.ValidationException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ItemController.class)
class ItemControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CommentService commentService;
    @MockBean
    private ItemService itemService;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private LocalDateTime start = LocalDateTime.parse(LocalDateTime.now().plusMinutes(2).format(dateTimeFormatter));
    private LocalDateTime end = LocalDateTime.parse(LocalDateTime.now().plusMinutes(10).format(dateTimeFormatter));
    private UserDto userDto1;
    private Long userIdNotFoud = 101L;
    private Long itemIdNotFound = 101L;
    private ItemDto itemDto;


    @SneakyThrows
    @Test
    public void getAllUsersEmptyTest() {
        when(itemService.getItemById(userIdNotFoud, itemIdNotFound))
                .thenThrow(new ItemNotFoundException("Item with id: " + itemIdNotFound + " not found"));
        MvcResult mvcResult = this.mockMvc.perform(get("/items/{itemId}", itemIdNotFound)
                        .header("X-Sharer-User-Id", 101L))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        assertTrue(responseBody.contains("Item with id: " + itemIdNotFound + " not found"));
    }

    @SneakyThrows
    @Test
    public void createItemNameIsEmptyTest() {
        itemDto = ItemDto.builder()
                .description("descriptionItem")
                .build();
        when(itemService.createItem(1L, itemDto))
                .thenThrow(new ValidationException("error\":\"name must not be blank"));
        MvcResult mvcResult = this.mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(responseBody.contains("error\":\"name must not be blank"));
    }

    @SneakyThrows
    @Test
    public void createItemDescriptionIsBlankTest() {
        itemDto = ItemDto.builder()
                .name("name")
                .build();
        when(itemService.createItem(1L, itemDto))
                .thenThrow(new ValidationException("error\":\"description must not be blank"));
        MvcResult mvcResult = this.mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(responseBody.contains("error\":\"description must not be blank"));
    }

    @SneakyThrows
    @Test
    public void getAllItemsTest() {
        itemDto = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .build();
        when(itemService.getAllItems(1L))
                .thenReturn(List.of(itemDto));
        List<ItemDto> itemDto1 = itemService.getAllItems(1L);
        System.out.println("itemDto1 = " + itemDto1);
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].name", is("name")))
                .andExpect(jsonPath("$[0].description", is("description")));
    }

    @SneakyThrows
    @Test
    public void getItemByIdTest() {

    }



}

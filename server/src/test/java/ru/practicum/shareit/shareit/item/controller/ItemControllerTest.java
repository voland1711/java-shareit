package ru.practicum.shareit.shareit.item.controller;

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
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentShortDto;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest({ItemController.class})
class ItemControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CommentService commentService;
    @MockBean
    private ItemService itemService;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private LocalDateTime start = LocalDateTime.parse(LocalDateTime.now().plusMinutes(2).format(dateTimeFormatter));
    private LocalDateTime end = LocalDateTime.parse(LocalDateTime.now().plusMinutes(10).format(dateTimeFormatter));
    private Long userIdNotFoud = 101L;
    private Long itemIdNotFound = 101L;
    private ItemDto itemDto;


    @SneakyThrows
    @Test
    public void createCommentTest() {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("text2")
                .build();
        CommentShortDto commentShortDto = CommentShortDto.builder()
                .text("text2")
                .build();

        when(commentService.createComment(any(), any(), any()))
                .thenReturn(commentDto);

        String resultResponse = mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentShortDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.text").value(commentDto.getText()))
                .andExpect(jsonPath("$.authorName").value(commentDto.getAuthorName()))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(commentDto), resultResponse);
    }


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
    public void getAllItemsTest() {
        itemDto = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .build();
        when(itemService.getAllItems(1L))
                .thenReturn(List.of(itemDto));
        List<ItemDto> itemDto1 = itemService.getAllItems(1L);
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
    public void createItemTest() {
        itemDto = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .build();
        when(itemService.createItem(1L, itemDto))
                .thenReturn(itemDto);
        String resultResponse = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), resultResponse);
    }

    @SneakyThrows
    @Test
    public void updateItemTest() {
        ItemDto itemDto = new ItemDto().toBuilder()
                .description("description").build();
        ItemDto updatedItemDto = new ItemDto().toBuilder()
                .description("description").build();
        when(itemService.updateItem(eq(1L), eq(itemDto), eq(2L))).thenReturn(updatedItemDto);

        String resultResponse = mockMvc.perform(patch("/items/{itemId}", 2L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), resultResponse);
    }

    @SneakyThrows
    @Test
    public void searchItemsTest() {
        itemDto = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("test")
                .build();
        when(itemService.searchItems(eq(1L), eq("test"))).thenReturn(List.of(itemDto));
        mockMvc.perform(get("/items/search", 2L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()))
                .andExpect(jsonPath("$[0].description").value(itemDto.getDescription()))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

}

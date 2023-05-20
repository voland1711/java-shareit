package ru.practicum.shareit.user.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private LocalDateTime start = LocalDateTime.parse(LocalDateTime.now().plusMinutes(2).format(dateTimeFormatter));
    private LocalDateTime end = LocalDateTime.parse(LocalDateTime.now().plusMinutes(10).format(dateTimeFormatter));
    private UserDto userDto1;
    private Long userIdNotFoud = 101L;


    @BeforeEach
    void setup() {
        userDto1 = UserDto.builder()
                .id(1L)
                .name("nameUser1")
                .email("nameuser1@user.ru")
                .build();
    }

    @SneakyThrows
    @Test
    public void getAllUsersEmptyTest() {
        Mockito
                .when(userService.getAllUsers()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)))
                .andReturn();
        verify(userService).getAllUsers();
    }

    @SneakyThrows
    @Test
    public void getAllUsersTest() {
        Mockito
                .when(userService.getAllUsers()).thenReturn(List.of(userDto1));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("nameUser1")))
                .andExpect(jsonPath("$[0].email", is("nameuser1@user.ru")));
        verify(userService).getAllUsers();
    }

    @SneakyThrows
    @Test
    public void getUserByIdNotFoundTest() {
        Mockito
                .when(userService.getUserById(anyLong()))
                .thenThrow(new ObjectNotFoundException("User with id: " + userIdNotFoud + " not found"));
        MvcResult mvcResult = mockMvc.perform(get("/users/{id}", userIdNotFoud))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        assertTrue(responseBody.contains("\"error\":\"User with id: 101 not found\""));
    }

    @SneakyThrows
    @Test
    public void getUserByIdTest() {
        Mockito
                .when(userService.getUserById(anyLong()))
                .thenReturn(userDto1);
        mockMvc.perform(get("/users/{id}", userIdNotFoud))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto1.getId()))
                .andExpect(jsonPath("$.name").value(userDto1.getName()))
                .andExpect(jsonPath("$.email").value(userDto1.getEmail()));
    }

    @SneakyThrows
    @Test
    public void createUserTest() {
        Mockito
                .when(userService.createUser(eq(userDto1)))
                .thenReturn(UserDto.builder()
                        .id(1L)
                        .name("nameUser1")
                        .email("nameuser1@user.ru")
                        .build());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto1)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("nameUser1"))
                .andExpect(jsonPath("$.email").value("nameuser1@user.ru"));
    }

    @SneakyThrows
    @Test
    public void updateUserTest() {
        UserDto userDto2 = UserDto.builder()
                .id(1L)
                .name("nameUpdate1")
                .email("nameUpdate1@user.ru")
                .build();
        Mockito
                .when(userService.updateUser(eq(userDto1), anyLong()))
                .thenReturn(userDto2);
        String resultResponse = mockMvc.perform(patch("/users//{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto1)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("nameUpdate1"))
                .andExpect(jsonPath("$.email").value("nameUpdate1@user.ru"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(userDto2), resultResponse);
    }

    @SneakyThrows
    @Test
    public void deleteUserTest() {
        Mockito
                .doNothing().when(userService).deleteUser(1L);
        mockMvc.perform(delete("/users/{id}", 2L))
                .andDo(print())
                .andExpect(status().isOk());
    }
}

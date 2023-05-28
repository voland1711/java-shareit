package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final String pathId = "/{id}";

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Работает: UserController.getAllUsers");
        return userService.getAllUsers();
    }

    @GetMapping(pathId)
    public UserDto getUserById(@PathVariable long id) {
        log.info("Работает: UserController.getUserById");
        return userService.getUserById(id);
    }

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        log.info("Работает: UserController.createUser");
        return userService.createUser(userDto);
    }

    @PatchMapping(pathId)
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable long id) {
        log.info("Работает: UserController.updateUser");
        return userService.updateUser(userDto, id);
    }

    @DeleteMapping(pathId)
    public void deleteUser(@PathVariable long id) {
        log.info("Работает: UserController.deleteUser");
        userService.deleteUser(id);
    }

}

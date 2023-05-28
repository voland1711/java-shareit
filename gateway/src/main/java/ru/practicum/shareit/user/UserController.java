package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

/**
 * TODO Sprint add-controllers.
 */

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserClient userClient;
    private final String pathId = "/{id}";

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Работает: UserController.getAllUsers");
        return userClient.getAllUsers();
    }

    @GetMapping(pathId)
    public ResponseEntity<Object> getUserById(@PathVariable long id) {
        log.info("Работает: UserController.getUserById");
        return userClient.getUserById(id);
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Работает: UserController.createUser");
        return userClient.createUser(userDto);
    }

    @PatchMapping(pathId)
    public ResponseEntity<Object> updateUser(@RequestBody UserDto userDto, @PathVariable long id) {
        log.info("Работает: UserController.updateUser");
        return userClient.updateUser(id, userDto);
    }

    @DeleteMapping(pathId)
    public void deleteUser(@PathVariable long id) {
        log.info("Работает: UserController.deleteUser");
        userClient.deleteUser(id);
    }

}
package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.Dto.CommentShortDto;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;
    private static final String SHARER_USER_ID = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader(SHARER_USER_ID) long userId) {
        log.info("Работает: ItemController.getAllItems");
        return itemClient.getAllItems(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(SHARER_USER_ID) long userId, @PathVariable long itemId) {
        log.info("Работает: ItemController.getItemById");
        return itemClient.getItemById(userId, itemId);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(SHARER_USER_ID) long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("Работает: ItemController.createItem");
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(SHARER_USER_ID) long userId, @RequestBody ItemDto itemDto,
                                             @PathVariable long itemId) {
        log.info("Работает: ItemController.updateItem");
        return itemClient.updateItem(userId, itemDto, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader(SHARER_USER_ID) long userId, @RequestParam("text") String text) {
        log.info("Работает: ItemController.searchItems");
        return itemClient.searchItems(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@PathVariable Long itemId, @RequestHeader(SHARER_USER_ID) Long userId,
                                                @Valid @RequestBody CommentShortDto commentShortDto) {
        log.info("Работает: ItemController.createComment");
        return itemClient.createComment(itemId, userId, commentShortDto);
    }

}
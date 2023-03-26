package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentShortDto;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final CommentService commentService;
    private static final String SHARER_USER_ID = "X-Sharer-User-Id";
    private static final String ITEM_ID = "{itemId}";
    private static final String ITEM_ID_COMMENT = "/{itemId}/comment";

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader(SHARER_USER_ID) long userId) {
        log.info("Работает: ItemController.getAllItems");
        return itemService.getAllItems(userId);
    }

    @GetMapping(ITEM_ID)
    public ItemDto getItemById(@RequestHeader(SHARER_USER_ID) long userId, @PathVariable long itemId) {
        log.info("Работает: ItemController.getItemById");
        return itemService.getItemById(userId, itemId);
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader(SHARER_USER_ID) long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("Работает: ItemController.createItem");
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping(ITEM_ID)
    public ItemDto updateItem(@RequestHeader(SHARER_USER_ID) long userId, @RequestBody Item item,
                              @PathVariable long itemId) {
        log.info("Работает: ItemController.updateItem");
        return itemService.updateItem(userId, item, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader(SHARER_USER_ID) long userId, @RequestParam("text") String text) {
        log.info("Работает: ItemController.searchItems");
        return itemService.searchItems(userId, text);
    }

    @PostMapping(ITEM_ID_COMMENT)
    public CommentDto createComment(@PathVariable Long itemId, @RequestHeader(SHARER_USER_ID) Long userId,
                                    @Valid @RequestBody CommentShortDto commentShortDto) {
        log.info("Работает: ItemController.createComment");
        return commentService.createComment(itemId, userId, commentShortDto);
    }

}

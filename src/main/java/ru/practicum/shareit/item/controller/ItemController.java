package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private static final String sharerUserId = "X-Sharer-User-Id";

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader(sharerUserId) long userId) {
        return itemService.getAllItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader(sharerUserId) long userId, @PathVariable long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader(sharerUserId) long userId, @Valid @RequestBody Item item) {
        return itemService.createItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(sharerUserId) long userId, @RequestBody Item item,
                              @PathVariable long itemId) {
        return itemService.updateItem(userId, item, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader(sharerUserId) long userId, @RequestParam("text") String text) {
        return itemService.searchItems(userId, text);
    }

}

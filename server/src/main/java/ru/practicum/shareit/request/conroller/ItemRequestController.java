package ru.practicum.shareit.request.conroller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private static final String SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestResponseDto createItemRequest(@RequestHeader(SHARER_USER_ID) Long userId,
                                                    @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Работает: ItemRequestController.createItemRequest");
        return itemRequestService.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto getById(@RequestHeader(SHARER_USER_ID) Long userId, @PathVariable Long requestId) {
        log.info("Работает: ItemRequestController.getByIdItemRequest");
        return itemRequestService.getByIdItemRequest(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getAllItemRequest(@RequestHeader(SHARER_USER_ID) Long userId,
                                                          @RequestParam(defaultValue = "0") Integer from,
                                                          @RequestParam(defaultValue = "10") Integer size) {
        log.info("Работает: ItemRequestController.getAllItemRequest");
        return itemRequestService.getAllItemRequest(userId, from, size);
    }

    @GetMapping
    public List<ItemRequestResponseDto> getAllByRequester(@RequestHeader(SHARER_USER_ID) Long userId,
                                                          @RequestParam(defaultValue = "0") Integer from,
                                                          @RequestParam(defaultValue = "10") Integer size) {
        log.info("Работает: ItemRequestController.getAllByRequester");
        return itemRequestService.getAllByRequester(userId, from, size);
    }

}

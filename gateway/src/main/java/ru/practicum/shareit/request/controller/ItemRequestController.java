package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.RequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * TODO Sprint add-item-requests.
 */
@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final RequestClient requestClient;
    private static final String SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object>  createItemRequest(@RequestHeader(SHARER_USER_ID) Long userId,
                                                    @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Работает: ItemRequestController.createItemRequest");
        return requestClient.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader(SHARER_USER_ID) Long userId, @PathVariable Long requestId) {
        log.info("Работает: ItemRequestController.getByIdItemRequest");
        return requestClient.getByIdItemRequest(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object>  getAllItemRequest(@RequestHeader(SHARER_USER_ID) Long userId,
                                                          @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                          @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Работает: ItemRequestController.getAllItemRequest");
        return requestClient.getAllItemRequest(userId, from, size);
    }

    @GetMapping
    public ResponseEntity<Object>  getAllByRequester(@RequestHeader(SHARER_USER_ID) Long userId,
                                                          @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                          @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Работает: ItemRequestController.getAllByRequester");
        return requestClient.getAllByRequester(userId, from, size);
    }

}

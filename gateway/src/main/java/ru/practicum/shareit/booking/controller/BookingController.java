package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.client.BookingClient;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingClient bookingClient;
    private static final String SHARER_USER_ID = "X-Sharer-User-Id";
    private static final String PATH_ID = "/{bookingId}";

    @GetMapping
    public ResponseEntity<Object> getAllByUser(@RequestHeader(SHARER_USER_ID) Long userId,
                                               @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                               @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                               @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Работает: BookingController.getAllByUser");
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getAllByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwner(@RequestHeader(SHARER_USER_ID) Long userId,
                                                @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Работает: BookingController.getAllByOwner");
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getAllByOwner(userId, state, from, size);
    }

    @GetMapping(PATH_ID)
    public ResponseEntity<Object> getById(@PathVariable Long bookingId, @RequestHeader(SHARER_USER_ID) Long userId) {
        log.info("Работает: BookingController.getById");
        return bookingClient.getById(bookingId, userId);
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(SHARER_USER_ID) Long userId,
                                                @Valid @RequestBody BookingShortDto bookingShortDto) {
        log.info("Работает: BookingController.createBooking");
        return bookingClient.createBooking(bookingShortDto, userId);
    }

    @PatchMapping(PATH_ID)
    public ResponseEntity<Object> approveBooking(@RequestHeader(SHARER_USER_ID) Long userId,
                                                 @PathVariable Long bookingId,
                                                 @RequestParam Boolean approved) {
        log.info("Работает: BookingController.approve");
        return bookingClient.approveBooking(userId, bookingId, approved);
    }
}

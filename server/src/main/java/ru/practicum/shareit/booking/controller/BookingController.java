package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;
    private static final String SHARER_USER_ID = "X-Sharer-User-Id";
    private static final String PATH_ID = "/{bookingId}";

    @GetMapping
    public List<BookingDto> getAllByUser(@RequestHeader(SHARER_USER_ID) Long userId,
                                         @RequestParam(defaultValue = "ALL") BookingState state,
                                         @RequestParam(defaultValue = "0") Integer from,
                                         @RequestParam(defaultValue = "10") Integer size) {
        log.info("Работает: BookingController.getAllByUser");
        return bookingService.getAllByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllByOwner(@RequestHeader(SHARER_USER_ID) Long userId,
                                          @RequestParam(defaultValue = "ALL") BookingState state,
                                          @RequestParam(defaultValue = "0") Integer from,
                                          @RequestParam(defaultValue = "10") Integer size) {
        log.info("Работает: BookingController.getAllByOwner");
        return bookingService.getAllByOwner(userId, state, from, size);
    }

    @GetMapping(PATH_ID)
    public BookingDto getById(@PathVariable Long bookingId, @RequestHeader(SHARER_USER_ID) Long userId) {
        log.info("Работает: BookingController.getById");
        return bookingService.getById(bookingId, userId);
    }

    @PostMapping
    public BookingDto createBooking(@RequestHeader(SHARER_USER_ID) Long userId,
                                    @RequestBody BookingShortDto bookingShortDto) {
        log.info("Работает: BookingController.createBooking");
        return bookingService.createBooking(bookingShortDto, userId);
    }

    @PatchMapping(PATH_ID)
    public BookingDto approve(
            @RequestHeader(SHARER_USER_ID) Long userId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved) {
        log.info("Работает: BookingController.approve");
        return bookingService.approveBooking(userId, bookingId, approved);
    }


}

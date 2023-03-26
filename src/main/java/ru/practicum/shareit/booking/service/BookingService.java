package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(BookingShortDto bookingShortDto, Long userId);

    BookingDto approveBooking(Long userId, Long bookingId, Boolean approved);

    List<BookingDto> getAllByOwner(Long userId, BookingState state);

    List<BookingDto> getAllByUser(Long userId, BookingState state);

    BookingDto getById(Long bookingId, Long userId);
}

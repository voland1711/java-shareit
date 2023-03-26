package ru.practicum.shareit.booking.model;

import lombok.NonNull;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingShortDto;

public class BookingMapper {
    public static BookingDto toBookingDto(@NonNull Booking booking) {
        return BookingDto
                .builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .build();
    }

    public static BookingDtoResponse toBookingDtoResponse(@NonNull Booking booking) {
        return BookingDtoResponse
                .builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }


    public static Booking toBooking(@NonNull BookingDto bookingDto) {
        return Booking
                .builder()
                .id(bookingDto.getId())
                .booker(bookingDto.getBooker())
                .item(bookingDto.getItem())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .build();
    }

    public static Booking toBookingShort(@NonNull BookingShortDto bookingShortDto) {
        return Booking
                .builder()
                .start(bookingShortDto.getStart())
                .end(bookingShortDto.getEnd())
                .build();
    }


}

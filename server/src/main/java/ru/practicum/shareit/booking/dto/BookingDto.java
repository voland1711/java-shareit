package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class BookingDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("start")
    private LocalDateTime start;

    @JsonProperty("end")
    private LocalDateTime end;

    @JsonProperty("item")
    private Item item;

    @JsonProperty("booker")
    private User booker;

    @JsonProperty("status")
    private BookingStatus status;
}

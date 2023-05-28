package ru.practicum.shareit.item;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
@EqualsAndHashCode
public class ItemDto {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private User owner;

    private Long requestId;

    private BookingDtoResponse nextBooking;

    private BookingDtoResponse lastBooking;

    private List<CommentDto> comments;
}

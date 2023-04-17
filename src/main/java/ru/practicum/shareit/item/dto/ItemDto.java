package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
@Builder(toBuilder = true)
public class ItemDto {

    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    private Boolean available;

    private User owner;

    private String request;

    private BookingDtoResponse nextBooking;

    private BookingDtoResponse lastBooking;

    private List<CommentDto> comments;
}

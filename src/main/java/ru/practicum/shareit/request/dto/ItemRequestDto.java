package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.ItemDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class ItemRequestDto {
    private Long id;
    @NotNull
    private String description;
    private LocalDateTime created;
    private List<ItemDto> items;
}

package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.shareit.item.ItemDto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class ItemRequestResponseDto implements Serializable {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("description")
    private String description;

    @JsonProperty("created")
    private LocalDateTime created;

    @JsonProperty("items")
    private Collection<ItemDto> items;
}
package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Collection;

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

    @JsonProperty("id")
    private Long id;

    @JsonProperty("description")
    private String description;

    @JsonProperty("created")
    private LocalDateTime created;

    @JsonProperty("items")
    private Collection<Object> items;
}

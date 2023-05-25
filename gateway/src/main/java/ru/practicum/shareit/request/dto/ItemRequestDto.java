package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;

public class ItemRequestDto implements Serializable {

    @JsonProperty("id")
    private Long id;

    @NotNull
    @NotEmpty
    @JsonProperty("description")
    private String description;

    @JsonProperty("created")
    private LocalDateTime created;

    @JsonProperty("items")
    private Collection<Object> items;
}

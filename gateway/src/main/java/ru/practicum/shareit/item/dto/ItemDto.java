package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.shareit.comment.Dto.CommentShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
@EqualsAndHashCode
public class ItemDto {

    @JsonProperty("id")
    private Long id;

    @NotNull
    @NotBlank
    @JsonProperty("name")
    private String name;

    @NotNull
    @NotBlank
    @JsonProperty("description")
    private String description;

    @NotNull
    @JsonProperty("available")
    private Boolean available;

    @JsonProperty("requestId")
    private Long requestId;

    @JsonProperty("comments")
    private List<CommentShortDto> comments;
}

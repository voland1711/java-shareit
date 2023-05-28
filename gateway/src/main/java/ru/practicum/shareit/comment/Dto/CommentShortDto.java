package ru.practicum.shareit.comment.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
public class CommentShortDto {

    @NotBlank
    @JsonProperty("text")
    private String text;
}

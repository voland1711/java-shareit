package ru.practicum.shareit.comment.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;

public class CommentShortDto {

    @NotBlank
    @JsonProperty("text")
    private String text;
}

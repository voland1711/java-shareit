package ru.practicum.shareit.comment.dto;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class CommentShortDto {

    private String text;
}

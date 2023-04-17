package ru.practicum.shareit.comment.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import ru.practicum.shareit.comment.dto.CommentDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {
    public static CommentDto toCommentDto(@NonNull Comment comment) {
        return CommentDto
                .builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }


    public static Comment toComment(@NonNull CommentDto commentDto) {
        return Comment
                .builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .build();
    }

}

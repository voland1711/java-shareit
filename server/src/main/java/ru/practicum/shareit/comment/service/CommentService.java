package ru.practicum.shareit.comment.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentShortDto;

public interface CommentService {

    CommentDto createComment(Long itemId, Long userId, CommentShortDto commentShortDto);
}

package ru.practicum.shareit.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.exception.BadRequestException;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentShortDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Objects;

import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;
import static ru.practicum.shareit.comment.model.CommentMapper.toCommentDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImp implements CommentService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    @Override
    public CommentDto createComment(Long itemId, Long userId, CommentShortDto commentShortDto) {
        log.info("Работает метод: createComment");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь с id = " + userId + " не найден"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("Вещь с id = " + itemId + " осутствует"));

        bookingRepository.findAll().stream()
                .filter(booking -> Objects.equals(booking.getItem().getId(), itemId))
                .filter(booking -> booking.getStatus() == APPROVED)
                .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                .filter(booking -> Objects.equals(booking.getBooker().getId(), userId))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Вы не можете оставить комментарий"));

        Comment comment = Comment.builder()
                .text(commentShortDto.getText())
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build();
        commentRepository.save(comment);

        log.info("Завершил работу метод: createComment");
        return toCommentDto(comment);
    }

}



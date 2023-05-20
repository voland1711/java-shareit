package ru.practicum.shareit.comment;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.exception.BadRequestException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentShortDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static ru.practicum.shareit.comment.model.CommentMapper.toCommentDto;
import static ru.practicum.shareit.user.model.UserMapper.toUser;

@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CommentServiceTest {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final CommentService commentService;
    private final BookingRepository bookingRepository;
    private Item item;
    private User firstUser;
    private User secondUser;
    private UserDto userDto1;
    private UserDto secondUserDto;
    private Booking booking;
    private Comment comment;
    private CommentShortDto commentShortDto;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private LocalDateTime start = LocalDateTime.parse(LocalDateTime.now().minusDays(3).format(dateTimeFormatter));
    private LocalDateTime end = LocalDateTime.parse(LocalDateTime.now().minusDays(1).format(dateTimeFormatter));

    @BeforeEach
    void setup() {
        firstUser = createFirstUser();
        secondUser = createSecondUser();
        userDto1 = createFirstUserDto();
        item = createItem();
        secondUserDto = createSecondUserDto();
        booking = createBooking();
        comment = createTestComment();
        commentShortDto = createCommentShortDto();
    }

    private User createFirstUser() {
        return User.builder()
                .id(1L)
                .name("nameFirstUser")
                .email("nameFirstUser@user.ru")
                .build();
    }

    private User createSecondUser() {
        return User.builder()
                .id(2L)
                .name("nameSecondUser")
                .email("nameSecondUser@user.ru")
                .build();
    }

    private UserDto createFirstUserDto() {
        return UserDto.builder()
                .id(1L)
                .name("nameFirstUser")
                .email("nameFirstUser@user.ru")
                .build();
    }

    private UserDto createSecondUserDto() {
        return UserDto.builder()
                .id(2L)
                .name("nameSecondUser")
                .email("nameSecondUser@user.ru")
                .build();
    }

    private Item createItem() {
        return Item.builder()
                .id(1L)
                .name("item1")
                .description("description1")
                .available(true)
                .owner(firstUser)
                .build();
    }

    private Booking createBooking() {
        return Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(secondUser)
                .status(BookingStatus.APPROVED)
                .build();
    }

    private Comment createTestComment() {
        return Comment.builder()
                .id(1L)
                .text("text1")
                .item(item)
                .author(firstUser)
                .created(end.minusDays(3))
                .build();
    }

    private CommentShortDto createCommentShortDto() {
        return CommentShortDto.builder()
                .text("text1")
                .build();
    }


    @Test
    public void createComment() {
        userRepository.save(toUser(userDto1));
        userRepository.save(toUser(secondUserDto));
        itemRepository.save(item);
        bookingRepository.save(booking);
        CommentDto resultCreateComment = toCommentDto(commentRepository.save(comment));

        assertNotNull(resultCreateComment);
        assertNotNull(resultCreateComment.getId());
        assertEquals(comment.getText(), resultCreateComment.getText());
        assertEquals(comment.getAuthor().getName(), resultCreateComment.getAuthorName());
    }

    @Test
    public void createCommentUserNotFound() {
        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> commentService.createComment(1L, 1L, commentShortDto))
                .withMessage("Пользователь с id = 1 не найден");
    }

    @Test
    public void createCommentItemNotFound() {
        userRepository.save(toUser(userDto1));
        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> commentService.createComment(1L, 1L, commentShortDto))
                .withMessage("Вещь с id = 1 осутствует");
    }

    @Test
    public void createCommentUserNotBooker() {
        userRepository.save(firstUser);
        itemRepository.save(item);
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> commentService.createComment(1L, 1L, commentShortDto))
                .withMessage("Вы не можете оставить комментарий");
    }

    @Test
    public void createCommentValidData() {
        userRepository.save(toUser(userDto1));
        userRepository.save(toUser(secondUserDto));
        itemRepository.save(item);
        booking.setBooker(secondUser);
        bookingRepository.save(booking);
        CommentDto resultCreateComment = commentService.createComment(1L, 2L, commentShortDto);
        assertNotNull(resultCreateComment);
        assertNotNull(resultCreateComment.getId());
        assertEquals(commentShortDto.getText(), resultCreateComment.getText());
        assertEquals(comment.getAuthor(), firstUser);
    }

}

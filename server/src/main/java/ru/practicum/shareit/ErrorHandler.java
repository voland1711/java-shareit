package ru.practicum.shareit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.exception.BadRequestException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.user.exception.ObjectNotFoundException;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> objectNotFoundException(final ObjectNotFoundException e) {
        log.error(String.valueOf(e));
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> badRequestException(final BadRequestException e) {
        log.error(String.valueOf(e));
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> itemNotFoundException(final ItemNotFoundException e) {
        log.error(String.valueOf(e));
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> methodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.error("error {}", e.getMessage());
        return Map.of("error", e.getFieldError().getField() + " " + e.getFieldError().getDefaultMessage());
    }

//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public Map<String, String> handleConstraintViolationException(final ConstraintViolationException e) {
//        log.error(String.valueOf(e));
//        return Map.of("error", e.getMessage());
//    }

}

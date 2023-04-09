package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.exception.BadRequestException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.CommentMapper;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.exception.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.model.BookingMapper.toBookingDtoResponse;
import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;
import static ru.practicum.shareit.item.model.ItemMapper.toItem;
import static ru.practicum.shareit.item.model.ItemMapper.toItemDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> getAllItems(long userId) {
        log.info("Работает метод: getAllItems, на вход пуступил параметр userId = {}", userId);
        existUser(userId);
        return itemRepository.findAll().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .map(item -> getLastAndNextBooking(item, userId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDto getItemById(long userId, long itemId) {
        log.info("Работает метод: getItemById, поступили параметры: userId = {} и itemId = {}", userId, itemId);
        existUser(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Вещь id= " + itemId + " в коллекции не найдена"));
        List<CommentDto> commentDtoList = commentRepository.findByItem(item).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        ItemDto itemDto = getLastAndNextBooking(item, userId);
        itemDto.setComments(commentDtoList);
        log.info("Метод: getItemById завершил работу");
        return itemDto;
    }

    @Transactional
    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        log.info("Работает метод: createItem, поступили параметры: userId = {} и item = {}", userId, itemDto);
        validationItem(itemDto);
        User tempUser = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь с id = " + userId + "не найден"));
        log.info("Вещь - {}, создана, пользователем userId = {}", itemDto.getName(), userId);
        Item item = toItem(itemDto);
        item.setOwner(tempUser);
        log.info("Метод: createItem завершил работу");
        throw new BadRequestException("CreateItem");
    }

    @Transactional
    @Override
    public ItemDto updateItem(long userId, Item item, long itemId) {
        log.info("Метод: updateItem, поступили параметры: userId = {}, item= {} и itemId = {}", userId, item, itemId);
        existUser(userId);
        Item oldItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("Вещь с id = " + itemId + " не найдена"));
        if (oldItem.getOwner().getId() != userId) {
            throw new ObjectNotFoundException("Обновить данные вещи может только ее владелец");
        }
        oldItem.setId(itemId);
        Optional.ofNullable(item.getName()).ifPresent(oldItem::setName);
        Optional.ofNullable(item.getDescription()).ifPresent(oldItem::setDescription);
        Optional.ofNullable(item.getAvailable()).ifPresent(oldItem::setAvailable);
        Optional.ofNullable(item.getOwner()).ifPresent(oldItem::setOwner);
        Optional.ofNullable(item.getRequest()).ifPresent(oldItem::setRequest);
        log.info("Вещь id = {} обновлена", itemId);
        log.info("Метод: updateItem завершил работу");
        return toItemDto(oldItem);
    }

    @Transactional
    @Override
    public List<ItemDto> searchItems(long userId, String text) {
        log.info("Работате метод: searchItems, поступили параметры: userId = {} и text = {}", userId, text);
        if (StringUtils.isBlank(text)) {
            return new ArrayList<>();
        }
        return itemRepository.findAll().stream()
                .filter(item -> StringUtils.containsIgnoreCase(item.getDescription(), text))
                .filter(Item::getAvailable)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void existUser(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь с id: " + userId + " не найден"));
    }

    private void validationItem(ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            throw new BadRequestException("Отсутсвует параметр available");
        }
        if (StringUtils.isEmpty(itemDto.getName())) {
            throw new ValidationException("Имя вещи пустое/содержит пробелы");
        }
        if (StringUtils.isEmpty(itemDto.getDescription())) {
            throw new ValidationException("Описание вещи пустое/содержит пробелы");
        }
    }

    private ItemDto getLastAndNextBooking(Item item, Long userId) {
        log.info("Работает метод: getLastAndNextBooking на вход поступил item = {} и userId = {}", item, userId);
        ItemDto itemDto = toItemDto(item);
        LocalDateTime currentLocalDateTime = LocalDateTime.now();
        if (Objects.equals(item.getOwner().getId(), userId)) {
            bookingRepository.findByItem(item).stream()
                    .sorted(Comparator.comparing(Booking::getStart).reversed())
                    .filter(booking -> booking.getStatus().equals(APPROVED))
                    .filter(booking -> booking.getStart().isBefore(currentLocalDateTime))
                    .findFirst()
                    .ifPresent(booking -> itemDto.setLastBooking(toBookingDtoResponse(booking)));
        }

        if (Objects.equals(item.getOwner().getId(), userId)) {
            bookingRepository.findByItem(item).stream()
                    .sorted(Comparator.comparing(Booking::getStart))
                    .filter(booking -> booking.getStatus().equals(APPROVED))
                    .filter(booking -> booking.getStart().isAfter(currentLocalDateTime))
                    .findFirst()
                    .ifPresent(booking -> itemDto.setNextBooking(toBookingDtoResponse(booking)));
        }
        log.info("Метод: getLastAndNextBooking завершил работу");
        return itemDto;
    }

}

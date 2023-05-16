package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.exception.BadRequestException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.model.BookingMapper.toBookingDto;
import static ru.practicum.shareit.booking.model.BookingStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImp implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final Sort bookingsSort = Sort.by(Sort.Direction.DESC, "start");

    @Transactional
    @Override
    public BookingDto createBooking(BookingShortDto bookingShortDto, Long userId) {
        log.info("Работает метод: createBooking");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь с id: " + userId + " не найден"));
        long itemId = bookingShortDto.getItemId();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("Вещь с id = " + itemId + " не найдена"));
        if (!item.getAvailable()) {
            throw new BadRequestException("Вещь уже забронирована");
        }
        if (item.getOwner().getId() == userId) {
            throw new ObjectNotFoundException("Владелец не может забронировать вещь");
        }

        LocalDateTime startlocalDateTime = bookingShortDto.getStart();
        LocalDateTime endlocalDateTime = bookingShortDto.getEnd();
        if (endlocalDateTime.isBefore(startlocalDateTime) || endlocalDateTime.equals(startlocalDateTime)) {
            throw new BadRequestException("Ошибка бронирования: дата окончания ранее даты начала бронирования");
        }
        Booking booking = BookingMapper.toBookingShort(bookingShortDto);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(WAITING);
        log.info("Завершил работу метод: createBooking");
        return toBookingDto(bookingRepository.save(booking));
    }

    @Transactional
    @Override
    public BookingDto approveBooking(Long userId, Long bookingId, Boolean approved) {
        log.info("Работает метод: approveBooking");
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ObjectNotFoundException("Бронирование с id = " + bookingId + " не найдено"));
        if (!booking.getStatus().equals(WAITING)) {
            throw new BadRequestException("Произошла ошибка бронирования");
        }
        if (!userId.equals(booking.getItem().getOwner().getId())) {
            throw new ObjectNotFoundException("Подтвердить бронирование может только владелец вещи");
        }
        if (Boolean.TRUE.equals(approved)) {
            log.info("Статус APPROVED");
            booking.setStatus(APPROVED);
        } else {
            log.info("Статус REJECTED");
            booking.setStatus(REJECTED);
        }
        log.info("Завершил работу метод: approveBooking");
        return toBookingDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getAllByUser(Long userId, BookingState state, Integer from, Integer size) {
        log.info("Работает метод: getAllByUser");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь с id: " + userId + " не найден"));
        List<Booking> bookingsList = new ArrayList<>();

        Pageable pageable = PageRequest.of(from / size, size, bookingsSort);

        log.info("state: {}", state);
        switch (state) {
            case ALL:
                bookingsList.addAll(bookingRepository.findAllByBooker(user, pageable));
                break;
            case CURRENT:
                bookingsList.addAll(bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(user,
                        LocalDateTime.now(), LocalDateTime.now(), pageable));
                break;
            case PAST:
                bookingsList.addAll(bookingRepository.findAllByBookerAndEndBefore(user,
                        LocalDateTime.now(), pageable));
                break;
            case FUTURE:
                bookingsList.addAll(bookingRepository.findAllByBookerAndStartAfter(user, LocalDateTime.now(), pageable));
                break;
            case WAITING:
                bookingsList.addAll(bookingRepository.findAllByBookerAndStatusEquals(user, WAITING, pageable));
                break;
            case REJECTED:
                bookingsList.addAll(bookingRepository.findAllByBookerAndStatusEquals(user, REJECTED, pageable));
                break;
            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
        log.info("Завершил работу метод: getAllByUser");
        return bookingsList.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getAllByOwner(Long userId, BookingState state, Integer from, Integer size) {
        log.info("Работает метод: getAllByOwner");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь с id: " + userId + " не найден"));
        List<Booking> bookingsList = new ArrayList<>();

        Pageable pageable = PageRequest.of(from / size, size, bookingsSort);

        log.info("state: {}", state);
        switch (state) {
            case ALL:
                bookingsList.addAll(bookingRepository.findAllByItemOwner(user, pageable));
                break;
            case CURRENT:
                bookingsList.addAll(bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(user,
                        LocalDateTime.now(), LocalDateTime.now(), pageable));
                break;
            case PAST:
                bookingsList.addAll(bookingRepository.findAllByItemOwnerAndEndBefore(user,
                        LocalDateTime.now(), pageable));
                break;
            case FUTURE:
                bookingsList.addAll(bookingRepository.findAllByItemOwnerAndStartAfter(user, LocalDateTime.now(), pageable));
                break;
            case WAITING:
                bookingsList.addAll(bookingRepository.findAllByItemOwnerAndStatusEquals(user, WAITING, pageable));
                break;
            case REJECTED:
                bookingsList.addAll(bookingRepository.findAllByItemOwnerAndStatusEquals(user, REJECTED, pageable));
                break;
            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
        log.info("Завершил работу метод: getAllByOwner");
        return bookingsList.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDto getById(Long bookingId, Long userId) {
        log.info("Работает метод: getById");
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ObjectNotFoundException("Бронирование с id = " + bookingId + " не найдено"));
        Long bookerId = booking.getBooker().getId();
        Long ownerId = booking.getItem().getOwner().getId();
        if ((Objects.equals(userId, bookerId) || Objects.equals(userId, ownerId))) {
            log.info("Завершил работу метод: getById");
            return toBookingDto(booking);
        } else {
            throw new ObjectNotFoundException("Бронирование с id = " + bookingId + " может просматривать только владелец");
        }
    }
}

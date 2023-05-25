package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findAllByItemOwner(User user, Pageable pageable);

    Page<Booking> findAllByItemOwnerAndStartBeforeAndEndAfter(User user, LocalDateTime now,
                                                              LocalDateTime now1, Pageable pageable);

    Page<Booking> findAllByItemOwnerAndEndBefore(User user, LocalDateTime now, Pageable pageable);

    Page<Booking> findAllByItemOwnerAndStartAfter(User user, LocalDateTime now, Pageable pageable);

    Page<Booking> findAllByItemOwnerAndStatusEquals(User user, BookingStatus waiting, Pageable pageable);

    Page<Booking> findAllByBooker(User user, Pageable pageable);

    Page<Booking> findAllByBookerAndStartBeforeAndEndAfter(User user, LocalDateTime now, LocalDateTime now1,
                                                           Pageable pageable);

    Page<Booking> findAllByBookerAndEndBefore(User user, LocalDateTime now, Pageable pageable);

    Page<Booking> findAllByBookerAndStartAfter(User user, LocalDateTime now, Pageable pageable);

    Page<Booking> findAllByBookerAndStatusEquals(User user, BookingStatus waiting, Pageable pageable);

    List<Booking> findByItem(Item item);
}

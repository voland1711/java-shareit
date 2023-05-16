package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<? extends Booking> findAllByItemOwner(User user,  Pageable pageable);

    List<? extends Booking> findAllByItemOwnerAndStartBeforeAndEndAfter(User user, LocalDateTime now,
                                                                        LocalDateTime now1,  Pageable pageable);

    List<? extends Booking> findAllByItemOwnerAndEndBefore(User user, LocalDateTime now,  Pageable pageable);

    List<? extends Booking> findAllByItemOwnerAndStartAfter(User user, LocalDateTime now,  Pageable pageable);

    List<? extends Booking> findAllByItemOwnerAndStatusEquals(User user, BookingStatus waiting,  Pageable pageable);

    List<? extends Booking> findAllByBooker(User user,  Pageable pageable);

    List<? extends Booking> findAllByBookerAndStartBeforeAndEndAfter(User user, LocalDateTime now, LocalDateTime now1,
                                                                     Pageable pageable);

    List<? extends Booking> findAllByBookerAndEndBefore(User user, LocalDateTime now,  Pageable pageable);

    List<? extends Booking> findAllByBookerAndStartAfter(User user, LocalDateTime now,  Pageable pageable);

    List<? extends Booking> findAllByBookerAndStatusEquals(User user, BookingStatus waiting,  Pageable pageable);

    List<Booking> findByItem(Item item);
}

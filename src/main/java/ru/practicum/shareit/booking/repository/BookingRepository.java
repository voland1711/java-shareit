package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Collection<? extends Booking> findAllByItemOwner(User user, Sort sort);

    Collection<? extends Booking> findAllByItemOwnerAndStartBeforeAndEndAfter(User user, LocalDateTime now, LocalDateTime now1, Sort sort);

    Collection<? extends Booking> findAllByItemOwnerAndEndBefore(User user, LocalDateTime now, Sort sort);

    Collection<? extends Booking> findAllByItemOwnerAndStartAfter(User user, LocalDateTime now, Sort sort);

    Collection<? extends Booking> findAllByItemOwnerAndStatusEquals(User user, BookingStatus waiting, Sort sort);

    Collection<? extends Booking> findAllByBooker(User user, Sort sort);

    Collection<? extends Booking> findAllByBookerAndStartBeforeAndEndAfter(User user, LocalDateTime now, LocalDateTime now1, Sort sort);

    Collection<? extends Booking> findAllByBookerAndEndBefore(User user, LocalDateTime now, Sort sort);

    Collection<? extends Booking> findAllByBookerAndStartAfter(User user, LocalDateTime now, Sort sort);

    Collection<? extends Booking> findAllByBookerAndStatusEquals(User user, BookingStatus waiting, Sort sort);

    List<Booking> findByItem(Item item);
}

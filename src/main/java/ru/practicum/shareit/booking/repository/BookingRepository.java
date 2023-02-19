package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(long userId);

    @Query("select booking from Booking booking " +
            "where booking.booker.id = ?1 " +
            "and ?2 between booking.start AND booking.end " +
            "order by booking.start desc")
    List<Booking> findByBookerIdCurrent(long userId, LocalDateTime time);

    @Query("select booking from Booking booking " +
            "where booking.booker.id = ?1 " +
            "and booking.end < ?2 " +
            "order by booking.start desc")
    List<Booking> findByBookerIdPast(long userId, LocalDateTime time);

    @Query("select booking from Booking booking " +
            "where booking.booker.id = ?1 " +
            "and booking.start > ?2 " +
            "order by booking.start desc")
    List<Booking> findByBookerIdFuture(long userId, LocalDateTime time);

    @Query("select booking from Booking booking " +
            "where booking.booker.id = ?1 " +
            "and booking.status = ?2 " +
            "order by booking.start desc")
    List<Booking> findByBookerIdAndStatus(long userId, BookingStatus status);

    List<Booking> findByItemOwnerIdOrderByStartDesc(long ownerId);

    @Query("select booking from Booking booking " +
            "where booking.item.ownerId = ?1 " +
            "and ?2 BETWEEN booking.start AND booking.end " +
            "order by booking.start desc")
    List<Booking> findByItemOwnerIdCurrent(long userId, LocalDateTime time);

    @Query("select booking from Booking booking " +
            "where booking.item.ownerId = ?1 " +
            "and booking.end < ?2 " +
            "order by booking.start desc")
    List<Booking> findByItemOwnerIdPast(long userId, LocalDateTime time);

    @Query("select booking from Booking booking " +
            "where booking.item.ownerId = ?1 " +
            "and booking.start > ?2 " +
            "order by booking.start desc")
    List<Booking> findByItemOwnerIdFuture(long userId, LocalDateTime time);

    @Query("select booking from Booking booking " +
            "where booking.item.ownerId = ?1 " +
            "and booking.status = ?2 " +
            "order by booking.start desc")
    List<Booking> findByItemOwnerIdAndStatus(long userId, BookingStatus status);

    @Query("select booking from Booking booking " +
            "where booking.item.id = ?1 " +
            "and booking.end < ?2 " +
            "order by booking.start desc")
    Optional<Booking> findLastBooking(long itemId, LocalDateTime time);

    @Query("select booking from Booking booking " +
            "where booking.item.id = ?1 " +
            "and booking.start > ?2 " +
            "order by booking.start desc")
    Optional<Booking> findNextBooking(long itemId, LocalDateTime time);

    @Query("select booking from Booking booking " +
            "WHERE booking.booker.id = ?1 " +
            "AND booking.item.id = ?2 " +
            "AND booking.end < ?3")
    List<Booking> findAllByBookerIdAndItemId(long userId, long itemId, LocalDateTime time);
}

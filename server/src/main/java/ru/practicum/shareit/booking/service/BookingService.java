package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(long userId, BookingDto bookingDto);

    BookingDto approvedBooking(long userId, long bookingId, boolean approved);

    BookingDto getBookingById(long userId, long bookingId);

    List<BookingDto> getBookingsByBookerId(long userId, String state, Pageable pageable);

    List<BookingDto> getBookingsByItemsOwnerId(long userId, String state, Pageable pageable);
}

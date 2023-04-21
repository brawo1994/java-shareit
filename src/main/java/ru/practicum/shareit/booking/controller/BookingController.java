package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.util.Pagination;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader(SHARER_USER_ID_HEADER) long userId,
                                    @Valid @RequestBody BookingDto bookingDto) {
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approvedBooking(@RequestHeader(SHARER_USER_ID_HEADER) long userId,
                                      @PathVariable long bookingId,
                                      @RequestParam boolean approved) {
        return bookingService.approvedBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader(SHARER_USER_ID_HEADER) long userId,
                                     @PathVariable long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getBookingsByBookerId(@RequestHeader(SHARER_USER_ID_HEADER) long userId,
                                                  @RequestParam(defaultValue = "ALL", required = false) String state,
                                                  @PositiveOrZero @RequestParam(defaultValue = "0", required = false) Integer from,
                                                  @Positive @RequestParam(defaultValue = "10", required = false) Integer size) {
        return bookingService.getBookingsByBookerId(userId, state, new Pagination(from, size, Sort.unsorted()));
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByItemsOwnerId(@RequestHeader(SHARER_USER_ID_HEADER) long userId,
                                                      @RequestParam(defaultValue = "ALL", required = false) String state,
                                                      @PositiveOrZero @RequestParam(defaultValue = "0", required = false) Integer from,
                                                      @Positive @RequestParam(defaultValue = "10", required = false) Integer size) {
        return bookingService.getBookingsByItemsOwnerId(userId, state, new Pagination(from, size, Sort.unsorted()));
    }
}

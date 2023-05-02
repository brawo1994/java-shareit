package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.util.Pagination;

import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader(SHARER_USER_ID_HEADER) long userId,
                                    @RequestBody BookingDto bookingDto) {
        log.info("Request received to create a new booking from user with id: {}", userId);
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approvedBooking(@RequestHeader(SHARER_USER_ID_HEADER) long userId,
                                      @PathVariable long bookingId,
                                      @RequestParam boolean approved) {
        log.info("Request received to change the booking status with id: {} from user with id: {}", bookingId, userId);
        return bookingService.approvedBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader(SHARER_USER_ID_HEADER) long userId,
                                     @PathVariable long bookingId) {
        log.info("Request received to get the booking with id: {} from user with id {}", bookingId, userId);
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getBookingsByBookerId(@RequestHeader(SHARER_USER_ID_HEADER) long userId,
                                                  @RequestParam(defaultValue = "ALL", required = false) String state,
                                                  @RequestParam(defaultValue = "0", required = false) Integer from,
                                                  @RequestParam(defaultValue = "10", required = false) Integer size) {
        log.info("Request received to get the bookings for booker with id: {}", userId);
        return bookingService.getBookingsByBookerId(userId, state, new Pagination(from, size, Sort.by(DESC, "start")));
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByItemsOwnerId(@RequestHeader(SHARER_USER_ID_HEADER) long userId,
                                                      @RequestParam(defaultValue = "ALL", required = false)
                                                          String state,
                                                      @RequestParam(defaultValue = "0", required = false)
                                                          Integer from,
                                                      @RequestParam(defaultValue = "10", required = false)
                                                          Integer size) {
        log.info("Request received to get the bookings for owner with id: {}", userId);
        return bookingService.getBookingsByItemsOwnerId(userId, state,
                new Pagination(from, size, Sort.by(DESC, "start")));
    }
}

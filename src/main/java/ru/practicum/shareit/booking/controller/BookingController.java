package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
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
                                                  @RequestParam(defaultValue = "ALL", required = false) String state) {
        return bookingService.getBookingsByBookerId(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByItemsOwnerId(@RequestHeader(SHARER_USER_ID_HEADER) long userId,
                                                      @RequestParam(defaultValue = "ALL", required = false) String state) {
        return bookingService.getBookingsByItemsOwnerId(userId, state);
    }
}

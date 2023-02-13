package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exeption.BadRequestException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.UnsupportedStatusException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;
    @Override
    @Transactional
    public BookingDto createBooking(long userId, BookingDto bookingDto) {
        Item item = itemService.getItemIfExistOrThrow(bookingDto.getItemId());
        User user = userService.getUserIfExistOrThrow(userId);
        if (item.getOwnerId() == userId) {
            throw new NotFoundException("Владелец не может забронировать свою вещь");
        }
        if (item.getAvailable().equals(false)) {
            throw new BadRequestException("Вещь не доступна для заказа");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new BadRequestException("Дата окончания бронирования не может быть раньше даты начала бронирования");
        }
        bookingDto.setStatus(BookingStatus.WAITING);
        bookingDto.setBooker(user);
        bookingDto.setItem(item);
        Booking booking = bookingRepository.save(BookingMapper.toModel(bookingDto));
        log.info("Booking with id: {} added to DB", booking.getId());
        return BookingMapper.toDto(booking);
    }

    @Override
    @Transactional
    public BookingDto approvedBooking(long userId, long bookingId, boolean approved) {
        Booking booking = getBookingIfExistOrThrow(bookingId);
        if (userId != booking.getItem().getOwnerId()) {
            throw new NotFoundException("У вас нет вещи с id " + booking.getItem().getId());
        }
        if (booking.getStatus() == BookingStatus.APPROVED) {
            throw new BadRequestException("Бронирование уже подтверждено");
        }
        if (approved)
            booking.setStatus(BookingStatus.APPROVED);
        else
            booking.setStatus(BookingStatus.REJECTED);
        booking = bookingRepository.save(booking);
        log.info("Changed status for booking with id: {} to: {} in DB", booking.getId(), booking.getStatus());
        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingById(long userId, long bookingId) {
        Booking booking = getBookingIfExistOrThrow(bookingId);
        if (booking.getBooker().getId() == userId || booking.getItem().getOwnerId() == userId) {
            return BookingMapper.toDto(booking);
        } else throw new NotFoundException("Вы не являетесь владельцем бронирования или вещи");
    }

    @Override
    public List<BookingDto> getBookingsByBookerId(long userId, String state) {
        userService.getUserIfExistOrThrow(userId);
        List<Booking> books = new ArrayList<>();
        switch (state) {
            case "ALL":
                books.addAll(bookingRepository.findAllByBookerIdOrderByStartDesc(userId));
                break;
            case "CURRENT":
                books.addAll(bookingRepository.findByBookerIdCurrent(userId, LocalDateTime.now()));
                break;
            case "PAST":
                books.addAll(bookingRepository.findByBookerIdPast(userId, LocalDateTime.now()));
                break;
            case "FUTURE":
                books.addAll(bookingRepository.findByBookerIdFuture(userId, LocalDateTime.now()));
                break;
            case "WAITING":
                books.addAll(bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING));
                break;
            case "REJECTED":
                books.addAll(bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED));
                break;
            default:
                throw new UnsupportedStatusException("Unknown state: " + state);
        }
        return books.stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsByItemsOwnerId(long userId, String state) {
        userService.getUserIfExistOrThrow(userId);
        List<Booking> books = new ArrayList<>();
        switch (state) {
            case "ALL":
                books.addAll(bookingRepository.findByItemOwnerIdOrderByStartDesc(userId));
                break;
            case "CURRENT":
                books.addAll(bookingRepository.findByItemOwnerIdCurrent(userId, LocalDateTime.now()));
                break;
            case "PAST":
                books.addAll(bookingRepository.findByItemOwnerIdPast(userId, LocalDateTime.now()));
                break;
            case "FUTURE":
                books.addAll(bookingRepository.findByItemOwnerIdFuture(userId, LocalDateTime.now()));
                break;
            case "WAITING":
                books.addAll(bookingRepository.findByItemOwnerIdAndStatus(userId, BookingStatus.WAITING));
                break;
            case "REJECTED":
                books.addAll(bookingRepository.findByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED));
                break;
            default:
                throw new UnsupportedStatusException("Unknown state: " + state);
        }
        return books.stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }

    private Booking getBookingIfExistOrThrow(long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new NotFoundException("Бронирования с id " + bookingId + " не существует в системе");
        });
    }
}

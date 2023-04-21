package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.impl.BookingServiceImpl;
import ru.practicum.shareit.exeption.BadRequestException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.UnsupportedStatusException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.impl.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.Pagination;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemServiceImpl itemService;

    @Mock
    private UserService userService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private Item item;
    private Booking booking;

    private Pagination pagination;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .id(1L)
                .name("Ivan")
                .email("ivan@ya.ru")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .owner(user)
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();

        pagination = new Pagination(0, 10, Sort.unsorted());
    }

    @Test
    void createBookingTest() {
        when(itemService.getItemIfExistOrThrow(anyLong())).thenReturn(item);
        when(userService.getUserIfExistOrThrow(anyLong())).thenReturn(user);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        BookingDto bookingDto = bookingService.createBooking(2L, BookingMapper.toDto(booking));

        assertEquals(booking.getId(), bookingDto.getId());
    }

    @Test
    void createBookingIfOwnerTest() {
        when(itemService.getItemIfExistOrThrow(anyLong())).thenReturn(item);
        when(userService.getUserIfExistOrThrow(anyLong())).thenReturn(user);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.createBooking(user.getId(), BookingMapper.toDto(booking))
        );

        assertEquals("Владелец не может забронировать свою вещь", exception.getMessage());
    }

    @Test
    void createBookingIfNotAvailableTest() {
        item.setAvailable(false);
        when(itemService.getItemIfExistOrThrow(anyLong())).thenReturn(item);
        when(userService.getUserIfExistOrThrow(anyLong())).thenReturn(user);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> bookingService.createBooking(2L, BookingMapper.toDto(booking))
        );

        assertEquals("Вещь не доступна для заказа", exception.getMessage());
    }

    @Test
    void createBookingIfNotCorrectStartAndEndTest() {
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().minusHours(1));
        when(itemService.getItemIfExistOrThrow(anyLong())).thenReturn(item);
        when(userService.getUserIfExistOrThrow(anyLong())).thenReturn(user);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> bookingService.createBooking(2L, BookingMapper.toDto(booking))
        );

        assertEquals("Дата окончания бронирования не может быть раньше даты начала бронирования", exception.getMessage());
    }


    @Test
    void approvedBookingApproveTest() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        BookingDto bookingDto = bookingService.approvedBooking(user.getId(), booking.getId(), true);

        assertEquals(booking.getId(), bookingDto.getId());
    }

    @Test
    void approvedBookingRejectTest() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        BookingDto bookingDto = bookingService.approvedBooking(user.getId(), booking.getId(), false);

        assertEquals(booking.getId(), bookingDto.getId());
    }

    @Test
    void approvedBookingNotFoundTest() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.approvedBooking(2L, booking.getId(), true)
        );

        assertEquals("У вас нет вещи с id " + booking.getItem().getId(), exception.getMessage());
    }

    @Test
    void approvedBookingAlreadyApproveTest() {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> bookingService.approvedBooking(user.getId(), booking.getId(), true)
        );

        assertEquals("Бронирование уже подтверждено", exception.getMessage());
    }

    @Test
    void getBookingByIdTest() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        BookingDto bookingDto = bookingService.getBookingById(user.getId(), booking.getId());

        assertEquals(booking.getId(), bookingDto.getId());
    }

    @Test
    void getBookingByIdNotFoundTest() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.getBookingById(user.getId(), booking.getId())
        );

        assertEquals("Бронирования с id " + booking.getId() + " не существует в системе", exception.getMessage());
    }

    @Test
    void getBookingByIdNotOwnerTest() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.getBookingById(2L, booking.getId())
        );

        assertEquals("Вы не являетесь владельцем бронирования или вещи", exception.getMessage());
    }

    @Test
    void getBookingsByBookerIdAllTest() {
        when(userService.getUserIfExistOrThrow(anyLong())).thenReturn(user);
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(),any(Pageable.class)))
                .thenReturn(List.of(booking));
        List<BookingDto> bookingDtoList = bookingService.getBookingsByBookerId(user.getId(), "ALL", pagination);

        assertEquals(1, bookingDtoList.size());
        assertEquals(booking.getId(), bookingDtoList.get(0).getId());
    }

    @Test
    void getBookingsByBookerIdCurrentTest() {
        when(userService.getUserIfExistOrThrow(anyLong())).thenReturn(user);
        when(bookingRepository.findByBookerIdCurrent(anyLong(), any(), any(Pageable.class)))
                .thenReturn(List.of(booking));
        List<BookingDto> bookingDtoList = bookingService.getBookingsByBookerId(user.getId(), "CURRENT", pagination);

        assertEquals(1, bookingDtoList.size());
        assertEquals(booking.getId(), bookingDtoList.get(0).getId());
    }

    @Test
    void getBookingsByBookerIdPastTest() {
        when(userService.getUserIfExistOrThrow(anyLong())).thenReturn(user);
        when(bookingRepository.findByBookerIdPast(anyLong(), any(), any(Pageable.class)))
                .thenReturn(List.of(booking));
        List<BookingDto> bookingDtoList = bookingService.getBookingsByBookerId(user.getId(), "PAST", pagination);

        assertEquals(1, bookingDtoList.size());
        assertEquals(booking.getId(), bookingDtoList.get(0).getId());
    }

    @Test
    void getBookingsByBookerIdFutureTest() {
        when(userService.getUserIfExistOrThrow(anyLong())).thenReturn(user);
        when(bookingRepository.findByBookerIdFuture(anyLong(), any(), any(Pageable.class)))
                .thenReturn(List.of(booking));
        List<BookingDto> bookingDtoList = bookingService.getBookingsByBookerId(user.getId(), "FUTURE", pagination);

        assertEquals(1, bookingDtoList.size());
        assertEquals(booking.getId(), bookingDtoList.get(0).getId());
    }

    @Test
    void getBookingsByBookerIdWaitingTest() {
        when(userService.getUserIfExistOrThrow(anyLong())).thenReturn(user);
        when(bookingRepository.findByBookerIdAndStatus(anyLong(), any(), any(Pageable.class)))
                .thenReturn(List.of(booking));
        List<BookingDto> bookingDtoList = bookingService.getBookingsByBookerId(user.getId(), "WAITING", pagination);

        assertEquals(1, bookingDtoList.size());
        assertEquals(booking.getId(), bookingDtoList.get(0).getId());
    }

    @Test
    void getBookingsByBookerIdRejectedTest() {
        when(userService.getUserIfExistOrThrow(anyLong())).thenReturn(user);
        when(bookingRepository.findByBookerIdAndStatus(anyLong(), any(), any(Pageable.class)))
                .thenReturn(List.of(booking));
        List<BookingDto> bookingDtoList = bookingService.getBookingsByBookerId(user.getId(), "REJECTED", pagination);

        assertEquals(1, bookingDtoList.size());
        assertEquals(booking.getId(), bookingDtoList.get(0).getId());
    }

    @Test
    void getBookingsByBookerIdUnsupportedStatusTest() {
        when(userService.getUserIfExistOrThrow(anyLong())).thenReturn(user);

        UnsupportedStatusException exception = assertThrows(
                UnsupportedStatusException.class,
                () -> bookingService.getBookingsByBookerId(user.getId(), "ANY", pagination)
        );

        assertEquals("Unknown state: ANY", exception.getMessage());
    }

    @Test
    void getBookingsByItemsOwnerIdAllTest() {
        when(userService.getUserIfExistOrThrow(anyLong())).thenReturn(user);
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(anyLong(),any(Pageable.class)))
                .thenReturn(List.of(booking));
        List<BookingDto> bookingDtoList = bookingService.getBookingsByItemsOwnerId(user.getId(), "ALL", pagination);

        assertEquals(1, bookingDtoList.size());
        assertEquals(booking.getId(), bookingDtoList.get(0).getId());
    }

    @Test
    void getBookingsByItemsOwnerIdCurrentTest() {
        when(userService.getUserIfExistOrThrow(anyLong())).thenReturn(user);
        when(bookingRepository.findByItemOwnerIdCurrent(anyLong(), any(), any(Pageable.class)))
                .thenReturn(List.of(booking));
        List<BookingDto> bookingDtoList = bookingService.getBookingsByItemsOwnerId(user.getId(), "CURRENT", pagination);

        assertEquals(1, bookingDtoList.size());
        assertEquals(booking.getId(), bookingDtoList.get(0).getId());
    }

    @Test
    void getBookingsByItemsOwnerIdPastTest() {
        when(userService.getUserIfExistOrThrow(anyLong())).thenReturn(user);
        when(bookingRepository.findByItemOwnerIdPast(anyLong(), any(), any(Pageable.class)))
                .thenReturn(List.of(booking));
        List<BookingDto> bookingDtoList = bookingService.getBookingsByItemsOwnerId(user.getId(), "PAST", pagination);

        assertEquals(1, bookingDtoList.size());
        assertEquals(booking.getId(), bookingDtoList.get(0).getId());
    }

    @Test
    void getBookingsByItemsOwnerIdFutureTest() {
        when(userService.getUserIfExistOrThrow(anyLong())).thenReturn(user);
        when(bookingRepository.findByItemOwnerIdFuture(anyLong(), any(), any(Pageable.class)))
                .thenReturn(List.of(booking));
        List<BookingDto> bookingDtoList = bookingService.getBookingsByItemsOwnerId(user.getId(), "FUTURE", pagination);

        assertEquals(1, bookingDtoList.size());
        assertEquals(booking.getId(), bookingDtoList.get(0).getId());
    }

    @Test
    void getBookingsByItemsOwnerIdWaitingTest() {
        when(userService.getUserIfExistOrThrow(anyLong())).thenReturn(user);
        when(bookingRepository.findByItemOwnerIdAndStatus(anyLong(), any(), any(Pageable.class)))
                .thenReturn(List.of(booking));
        List<BookingDto> bookingDtoList = bookingService.getBookingsByItemsOwnerId(user.getId(), "WAITING", pagination);

        assertEquals(1, bookingDtoList.size());
        assertEquals(booking.getId(), bookingDtoList.get(0).getId());
    }

    @Test
    void getBookingsByItemsOwnerIdRejectedTest() {
        when(userService.getUserIfExistOrThrow(anyLong())).thenReturn(user);
        when(bookingRepository.findByItemOwnerIdAndStatus(anyLong(), any(), any(Pageable.class)))
                .thenReturn(List.of(booking));
        List<BookingDto> bookingDtoList = bookingService.getBookingsByItemsOwnerId(user.getId(), "REJECTED", pagination);

        assertEquals(1, bookingDtoList.size());
        assertEquals(booking.getId(), bookingDtoList.get(0).getId());
    }

    @Test
    void getBookingsByItemsOwnerIdUnsupportedStatusTest() {
        when(userService.getUserIfExistOrThrow(anyLong())).thenReturn(user);

        UnsupportedStatusException exception = assertThrows(
                UnsupportedStatusException.class,
                () -> bookingService.getBookingsByItemsOwnerId(user.getId(), "ANY", pagination)
        );

        assertEquals("Unknown state: ANY", exception.getMessage());
    }
}

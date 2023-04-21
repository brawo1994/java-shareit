package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Pagination;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase
class BookingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private BookingRepository bookingRepository;

    private final Pageable pageable = new Pagination(0, 10, Sort.unsorted());

    private final User user = User.builder()
            .name("Ivan")
            .email("ivan@ya.ru")
            .build();

    private final User user2 = User.builder()
            .name("Petr")
            .email("petr@ya.ru")
            .build();

    private final Item item = Item.builder()
            .name("Item name")
            .description("Item Description")
            .available(true)
            .owner(user)
            .build();

    private final Booking booking = Booking.builder()
            .item(item)
            .booker(user2)
            .build();

    @Test
    void contextLoads() {
        assertNotNull(entityManager);
    }

    @Test
    void findByBookerIdCurrentTest() {
        booking.setStart(LocalDateTime.now().minusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(1));

        entityManager.persist(user);
        entityManager.persist(user2);
        entityManager.persist(item);
        entityManager.persist(booking);

        List<Booking> bookingList = bookingRepository
                .findByBookerIdCurrent(user2.getId(), LocalDateTime.now(), pageable);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    void findByBookerIdPastTest() {
        booking.setStart(LocalDateTime.now().minusHours(2));
        booking.setEnd(LocalDateTime.now().minusHours(1));

        entityManager.persist(user);
        entityManager.persist(user2);
        entityManager.persist(item);
        entityManager.persist(booking);

        List<Booking> bookingList = bookingRepository
                .findByBookerIdPast(user2.getId(), LocalDateTime.now(), pageable);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    void findByBookerIdFutureTest() {
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(2));

        entityManager.persist(user);
        entityManager.persist(user2);
        entityManager.persist(item);
        entityManager.persist(booking);

        List<Booking> bookingList = bookingRepository
                .findByBookerIdFuture(user2.getId(), LocalDateTime.now(), pageable);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    void findByBookerIdAndStatusTest() {
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusHours(1));

        entityManager.persist(user);
        entityManager.persist(user2);
        entityManager.persist(item);
        entityManager.persist(booking);

        List<Booking> bookingList = bookingRepository
                .findByBookerIdAndStatus(user2.getId(), BookingStatus.WAITING, pageable);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    void findByItemOwnerIdCurrentTest() {
        booking.setStart(LocalDateTime.now().minusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(1));

        entityManager.persist(user);
        entityManager.persist(user2);
        entityManager.persist(item);
        entityManager.persist(booking);

        List<Booking> bookingList = bookingRepository
                .findByItemOwnerIdCurrent(user.getId(), LocalDateTime.now(), pageable);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));

    }

    @Test
    void findByItemOwnerIdPastTest() {
        booking.setStart(LocalDateTime.now().minusHours(2));
        booking.setEnd(LocalDateTime.now().minusHours(1));

        entityManager.persist(user);
        entityManager.persist(user2);
        entityManager.persist(item);
        entityManager.persist(booking);

        List<Booking> bookingList = bookingRepository
                .findByItemOwnerIdPast(user.getId(), LocalDateTime.now(), pageable);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    void findByItemOwnerIdFutureTest() {
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(2));

        entityManager.persist(user);
        entityManager.persist(user2);
        entityManager.persist(item);
        entityManager.persist(booking);

        List<Booking> bookingList = bookingRepository
                .findByItemOwnerIdFuture(user.getId(), LocalDateTime.now(), pageable);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    void findByItemOwnerIdAndStatusTest() {
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusHours(1));

        entityManager.persist(user);
        entityManager.persist(user2);
        entityManager.persist(item);
        entityManager.persist(booking);

        List<Booking> bookingList = bookingRepository
                .findByItemOwnerIdAndStatus(user.getId(), BookingStatus.WAITING, pageable);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    void findLastBookingTest() {
        booking.setStart(LocalDateTime.now().minusHours(2));
        booking.setEnd(LocalDateTime.now().minusHours(1));

        entityManager.persist(user);
        entityManager.persist(user2);
        entityManager.persist(item);
        entityManager.persist(booking);

        Optional<Booking> lastBooking = bookingRepository
                .findLastBooking(item.getId(), LocalDateTime.now());

        assertTrue(lastBooking.isPresent());
        assertEquals(lastBooking.get().getStart(), booking.getStart());
        assertEquals(lastBooking.get().getEnd(), booking.getEnd());
    }

    @Test
    void findNextBookingTest() {
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(2));

        entityManager.persist(user);
        entityManager.persist(user2);
        entityManager.persist(item);
        entityManager.persist(booking);

        Optional<Booking> lastBooking = bookingRepository
                .findNextBooking(item.getId(), LocalDateTime.now());

        assertTrue(lastBooking.isPresent());
        assertEquals(lastBooking.get().getStart(), booking.getStart());
        assertEquals(lastBooking.get().getEnd(), booking.getEnd());
    }

    @Test
    void findAllByBookerIdAndItemIdTest() {
        booking.setStart(LocalDateTime.now().minusHours(2));
        booking.setEnd(LocalDateTime.now().minusHours(1));

        entityManager.persist(user);
        entityManager.persist(user2);
        entityManager.persist(item);
        entityManager.persist(booking);

        List<Booking> bookingList = bookingRepository
                .findAllByBookerIdAndItemId(user2.getId(), item.getId(), LocalDateTime.now());

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }
}

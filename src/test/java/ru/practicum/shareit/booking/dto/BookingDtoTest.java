package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    private BookingDto bookingDto;

    @BeforeEach
    void beforeEach() {
        User user = User.builder()
                .id(1L)
                .name("Ivan")
                .email("ivan@ya.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("Item name")
                .description("Item description")
                .available(true)
                .owner(user)
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(3))
                .item(item)
                .status(BookingStatus.WAITING)
                .build();

        bookingDto = BookingMapper.toDto(booking);
    }

    @Test
    void bookingDtoTest() throws Exception {
        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo((int)bookingDto.getId());
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo((int)bookingDto.getItem().getId());
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isNull();
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(bookingDto.getStatus().toString());
    }
}

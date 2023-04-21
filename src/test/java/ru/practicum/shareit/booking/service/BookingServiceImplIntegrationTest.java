package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.booking.model.enums.BookingStatus.APPROVED;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplIntegrationTest {

    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    @Test
    void approvedBookingTest() {

        UserDto ownerDto = UserDto.builder()
                .name("Ivan")
                .email("ivan4@ya.ru")
                .build();
        UserDto resultOwnerDto = userService.createUser(ownerDto);

        UserDto bookerDto = UserDto.builder()
                .name("Petr")
                .email("petr4@ya.ru")
                .build();
        UserDto resultBookerDto = userService.createUser(bookerDto);

        ItemDto itemDto = ItemDto.builder()
                .name("Item name")
                .description("Item description")
                .available(true)
                .build();
        ItemDto resultItemDto = itemService.createItem(resultOwnerDto.getId(), itemDto);

        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(10))
                .itemId(resultItemDto.getId())
                .build();
        BookingDto resultBookingDto = bookingService.createBooking(resultBookerDto.getId(), bookingDto);

        BookingDto resultBookingDtoFinal = bookingService.approvedBooking(resultOwnerDto.getId(), resultBookingDto.getId(), true);

        assertEquals(resultItemDto, ItemMapper.toDto(resultBookingDtoFinal.getItem()));
        assertEquals(resultBookerDto, UserMapper.toDto(resultBookingDtoFinal.getBooker()));
        assertEquals(APPROVED, resultBookingDtoFinal.getStatus());
    }
}

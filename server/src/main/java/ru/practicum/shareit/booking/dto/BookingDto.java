package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.enums.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {

    private long id;

    @JsonFormat
    private LocalDateTime start;

    @JsonFormat
    private LocalDateTime end;

    private long itemId;

    private Item item;

    private User booker;

    private BookingStatus status;
}

package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.enums.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {

    @NotNull
    private long id;

    @FutureOrPresent
    @JsonFormat
    private LocalDateTime start;

    @Future
    @JsonFormat
    private LocalDateTime end;

    @NotNull
    private long itemId;

    private Item item;

    private User booker;

    private BookingStatus status;
}

package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.List;

@Data
@Builder
public class ItemDto {
    private long id;

    private String name;

    private String description;

    private Long requestId;

    private Boolean available;

    private BookingShortDto lastBooking;

    private BookingShortDto nextBooking;

    private List<CommentDto> comments;
}
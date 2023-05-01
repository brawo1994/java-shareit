package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingShortDto {
    private long id;
    private long bookerId;
}

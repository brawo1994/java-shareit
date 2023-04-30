package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
public class ItemDto {
    private long id;

    @NotEmpty(message = "Поле name не может отсутствовать или быть пустым")
    @Size(max = 255, message = "Значение в поле name не может быть длиннее 255 символов")
    private String name;

    @NotEmpty(message = "Поле description не может отсутствовать или быть пустым")
    @Size(max = 1024, message = "Значение в поле description не может быть длиннее 1024 символов")
    private String description;

    private Long requestId;

    @NotNull(message = "Поле available не может отсутствовать")
    private Boolean available;

    private BookingShortDto lastBooking;

    private BookingShortDto nextBooking;

    private List<CommentDto> comments;
}

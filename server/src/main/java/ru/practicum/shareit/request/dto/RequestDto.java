package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class RequestDto {
    private long id;

    @JsonFormat
    private LocalDateTime created;

    private String description;

    private List<ItemDto> items;
}

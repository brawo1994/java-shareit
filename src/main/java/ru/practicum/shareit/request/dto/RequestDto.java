package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class RequestDto {
    private long id;

    @JsonFormat
    private LocalDateTime created;

    @NotEmpty(message = "Поле description не может отсутствовать или быть пустым")
    @Size(max = 1024, message = "Значение в поле description не может быть длиннее 1024 символов")
    private String description;

    private List<ItemDto> items;
}

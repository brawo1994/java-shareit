package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemGateDto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class RequestGateDto {

    private long id;

    @JsonFormat
    private LocalDateTime created;

    @NotEmpty(message = "Поле description не может отсутствовать или быть пустым")
    @Size(max = 1024, message = "Значение в поле description не может быть длиннее 1024 символов")
    private String description;

    private List<ItemGateDto> items;
}

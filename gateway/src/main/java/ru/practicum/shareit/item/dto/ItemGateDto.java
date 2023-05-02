package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
public class ItemGateDto {

    @NotEmpty(message = "Поле name не может отсутствовать или быть пустым")
    @Size(max = 255, message = "Значение в поле name не может быть длиннее 255 символов")
    private String name;

    @NotEmpty(message = "Поле description не может отсутствовать или быть пустым")
    @Size(max = 1024, message = "Значение в поле description не может быть длиннее 1024 символов")
    private String description;

    @NotNull(message = "Поле available не может отсутствовать")
    private Boolean available;

    private Long requestId;
}

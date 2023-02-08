package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
public class ItemDto {
    private Long id;

    @NotEmpty(message = "Поле name не может отсутствовать или быть пустым")
    @Size(max = 100, message = "Наименование не может быть длиннее 100 символов")
    private String name;

    @NotEmpty(message = "Поле description не может отсутствовать или быть пустым")
    @Size(max = 1000, message = "Описание не может быть длиннее 1000 символов")
    private String description;

    @NotNull(message = "Поле available не может отсутствовать")
    private Boolean available;
}

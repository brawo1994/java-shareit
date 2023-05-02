package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@Builder
public class CommentGateDto {

    @NotEmpty(message = "Поле text не может отсутствовать или быть пустым")
    @Size(max = 1024, message = "Значение в поле text не может быть длиннее 1024 символов")
    private String text;

    private String authorName;
}

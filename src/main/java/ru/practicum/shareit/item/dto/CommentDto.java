package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {

    private Long id;

    @NotEmpty(message = "Поле text не может отсутствовать или быть пустым")
    @Size(max = 1024, message = "Значение в поле text не может быть длиннее 1024 символов")
    private String text;

    private String authorName;

    @JsonFormat
    private LocalDateTime created;
}

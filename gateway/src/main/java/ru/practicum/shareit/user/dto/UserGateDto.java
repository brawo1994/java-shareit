package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@Builder
public class UserGateDto {
    @NotBlank(message = "Поле name не может отсутствовать или быть пустым")
    @Size(max = 255, message = "Значение в поле name не может быть длиннее 255 символов")
    private String name;

    @Email(message = "Значение в поле email не соответствует формату")
    @NotEmpty(message = "Поле email не может отсутствовать или быть пустым")
    @Size(max = 512, message = "Значение в поле email не может быть длиннее 512 символов")
    private String email;
}

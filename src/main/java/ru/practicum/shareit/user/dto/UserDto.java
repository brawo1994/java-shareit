package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class UserDto {
    private long id;

    @NotBlank(message = "Поле name не может отсутствовать или быть пустым")
    private String name;

    @Email(message = "Значение в поле email не соответствует формату")
    @NotEmpty(message = "Поле email не может отсутствовать или быть пустым")
    private String email;
}

package ru.practicum.shareit.exeption.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponseDto {
    private String error;
    private String description;
}

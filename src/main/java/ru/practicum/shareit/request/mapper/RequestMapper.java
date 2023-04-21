package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;

import java.util.ArrayList;

public class RequestMapper {

    private RequestMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Request toModel(RequestDto requestDto) {
        return Request.builder()
                .description(requestDto.getDescription())
                .build();
    }

    public static RequestDto toDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(new ArrayList<>())
                .build();
    }
}

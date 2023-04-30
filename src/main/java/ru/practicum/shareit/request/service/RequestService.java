package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto createRequest(long userId, RequestDto requestDto);

    List<RequestDto> getRequestListByOwnerId(long userId);

    List<RequestDto> getAllRequestList(long userId, Pageable pageable);

    RequestDto getRequestById(long userId, long requestId);
}


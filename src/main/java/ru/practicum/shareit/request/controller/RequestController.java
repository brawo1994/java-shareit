package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.util.Pagination;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class RequestController {

    private static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";
    private final RequestService requestService;

    @PostMapping
    public RequestDto createRequest(@RequestHeader(SHARER_USER_ID_HEADER) long userId,
                                    @Valid @RequestBody RequestDto requestDto) {
        return requestService.createRequest(userId, requestDto);
    }

    @GetMapping
    public List<RequestDto> getRequestList(@RequestHeader(SHARER_USER_ID_HEADER) long userId) {
        return requestService.getRequestListByOwnerId(userId);
    }


    @GetMapping("/all")
    public List<RequestDto> getAllRequestList(@RequestHeader(SHARER_USER_ID_HEADER) long userId,
                                              @PositiveOrZero @RequestParam(defaultValue = "0", required = false) Integer from,
                                              @Positive @RequestParam(defaultValue = "10", required = false) Integer size) {
        return requestService.getAllRequestList(userId, new Pagination(from, size, Sort.unsorted()));
    }

    @GetMapping("/{requestId}")
    public RequestDto getRequest(@RequestHeader(SHARER_USER_ID_HEADER) long userId,
                                 @PathVariable long requestId) {
        return requestService.getRequestById(userId, requestId);
    }
}

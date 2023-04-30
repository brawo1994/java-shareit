package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.util.Pagination;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class RequestController {

    private static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";
    private final RequestService requestService;

    @PostMapping
    public RequestDto createRequest(@RequestHeader(SHARER_USER_ID_HEADER) long userId,
                                    @Valid @RequestBody RequestDto requestDto) {
        log.info("Request received to create a new request from user with id: {}", userId);
        return requestService.createRequest(userId, requestDto);
    }

    @GetMapping
    public List<RequestDto> getRequestList(@RequestHeader(SHARER_USER_ID_HEADER) long userId) {
        log.info("Request received to get requests from owner with id: {}", userId);
        return requestService.getRequestListByOwnerId(userId);
    }


    @GetMapping("/all")
    public List<RequestDto> getAllRequestList(@RequestHeader(SHARER_USER_ID_HEADER) long userId,
                                              @PositiveOrZero @RequestParam(defaultValue = "0", required = false) Integer from,
                                              @Positive @RequestParam(defaultValue = "10", required = false) Integer size) {
        log.info("Request received to get requests where owner not current user from user with id: {}", userId);
        return requestService.getAllRequestList(userId, new Pagination(from, size, Sort.by(Sort.Direction.DESC, "created")));
    }

    @GetMapping("/{requestId}")
    public RequestDto getRequest(@RequestHeader(SHARER_USER_ID_HEADER) long userId,
                                 @PathVariable long requestId) {
        log.info("Request received to get request with id: {} from user with id: {}", requestId, userId);
        return requestService.getRequestById(userId, requestId);
    }
}

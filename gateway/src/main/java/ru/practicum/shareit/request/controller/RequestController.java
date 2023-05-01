package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.client.RequestClient;
import ru.practicum.shareit.request.dto.RequestGateDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class RequestController {

    private static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader(SHARER_USER_ID_HEADER) long userId,
                                                @Valid @RequestBody RequestGateDto requestGateDto) {
        log.info("Create request {}, userId={}", requestGateDto, userId);
        return requestClient.createRequest(userId, requestGateDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestList(@RequestHeader(SHARER_USER_ID_HEADER) long userId) {
        log.info("Get request list from ownerId={}", userId);
        return requestClient.getRequestList(userId);
    }


    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequestList(@RequestHeader(SHARER_USER_ID_HEADER) long userId,
                                              @PositiveOrZero @RequestParam(defaultValue = "0", required = false) Integer from,
                                              @Positive @RequestParam(defaultValue = "10", required = false) Integer size) {
        log.info("Get requests where owner not current user from userId={}", userId);
        return requestClient.getAllRequestList(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader(SHARER_USER_ID_HEADER) long userId,
                                 @PathVariable long requestId) {
        log.info("Get request with id={} from userId={}", requestId, userId);
        return requestClient.getRequest(userId, requestId);
    }
}

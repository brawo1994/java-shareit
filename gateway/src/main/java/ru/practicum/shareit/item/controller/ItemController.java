package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentGateDto;
import ru.practicum.shareit.item.dto.ItemGateDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(SHARER_USER_ID_HEADER) long userId,
                                             @Valid @RequestBody ItemGateDto itemGateDto) {
        log.info("Create item {}, userId={}", itemGateDto, userId);
        return itemClient.createItem(userId, itemGateDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(SHARER_USER_ID_HEADER) long userId,
                              @PathVariable long itemId,
                              @RequestBody ItemGateDto itemGateDto) {
        log.info("Update item {}, userId={}, itemId={}", itemGateDto, userId, itemId);
        return itemClient.updateItem(userId, itemId, itemGateDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(SHARER_USER_ID_HEADER) long userId,
                           @PathVariable long itemId) {
        log.info("Delete item with id={}, from userId={}", itemId, userId);
        itemClient.deleteItem(userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(SHARER_USER_ID_HEADER) long userId,
                           @PathVariable long itemId) {
        log.info("Get item with id={}, from userId={}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByUserId(@RequestHeader(SHARER_USER_ID_HEADER)long userId,
                                          @PositiveOrZero @RequestParam(defaultValue = "0", required = false)
                                              Integer from,
                                          @Positive @RequestParam(defaultValue = "10", required = false)
                                              Integer size) {
        log.info("Get items from owner with id: {}", userId);
        return itemClient.getItemsByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text,
                                     @PositiveOrZero @RequestParam(defaultValue = "0", required = false) Integer from,
                                     @Positive @RequestParam(defaultValue = "10", required = false) Integer size) {
        log.info("Search items with text={}", text);
        return itemClient.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(SHARER_USER_ID_HEADER) long userId,
                                    @PathVariable long itemId,
                                    @Valid @RequestBody CommentGateDto commentGateDto) {
        log.info("Create comment = {} to item with id: {} from user with id: {}", commentGateDto, itemId, userId);
        return itemClient.createComment(userId, itemId, commentGateDto);
    }
}

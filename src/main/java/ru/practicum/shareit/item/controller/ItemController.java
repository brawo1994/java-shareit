package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.Pagination;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader(SHARER_USER_ID_HEADER) long userId,
                              @Valid @RequestBody ItemDto itemDto) {
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(SHARER_USER_ID_HEADER) long userId,
                              @PathVariable long itemId,
                              @RequestBody ItemDto itemDto) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(SHARER_USER_ID_HEADER) long userId,
                           @PathVariable long itemId) {
        itemService.deleteItem(userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader(SHARER_USER_ID_HEADER) long userId,
                           @PathVariable long itemId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getItemsByUserId(@RequestHeader(SHARER_USER_ID_HEADER) long userId,
                                          @PositiveOrZero @RequestParam(defaultValue = "0", required = false) Integer from,
                                          @Positive @RequestParam(defaultValue = "10", required = false) Integer size) {
        return itemService.getItemsByUserId(userId, new Pagination(from, size, Sort.unsorted()));
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text,
                                     @PositiveOrZero @RequestParam(defaultValue = "0", required = false) Integer from,
                                     @Positive @RequestParam(defaultValue = "10", required = false) Integer size) {
        return itemService.searchItemsByText(text, new Pagination(from, size, Sort.unsorted()));
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(SHARER_USER_ID_HEADER) long userId,
                                    @PathVariable long itemId,
                                    @Valid @RequestBody CommentDto commentDto) {
        return itemService.createComment(userId, itemId, commentDto);
    }
}

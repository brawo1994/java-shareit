package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto createItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, long itemId, ItemDto itemDto);

    void deleteItem(long userId, long itemId);

    ItemDto getItemById(long itemId, long userId);

    List<ItemDto> getItemsByUserId(long userId, Pageable pageable);

    List<ItemDto> searchItemsByText(String text, Pageable pageable);

    Item getItemIfExistOrThrow(long itemId);

    CommentDto createComment(long userId, long itemId, CommentDto commentDto);
}

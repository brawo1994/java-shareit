package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.NotExistException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    public ItemDto createItem(long userId, ItemDto itemDto) {
        userService.throwIfUserNotExist(userId);
        Item item = ItemMapper.toModel(itemDto);
        item.setOwnerId(userId);
        return ItemMapper.toDto(itemRepository.createItem(item));
    }

    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        userService.throwIfUserNotExist(userId);
        throwIfItemNotExist(itemId);
        if (itemRepository.findItemById(itemId).getOwnerId() != userId)
            throw new NotExistException("Вещь с id " + itemId + " не найдена у пользователя с id " + userId);
        return ItemMapper.toDto(itemRepository.updateItem(createItemToUpdate(itemId, itemDto)));
    }

    public void deleteItem(long userId, long itemId) {
        userService.throwIfUserNotExist(userId);
        throwIfItemNotExist(itemId);
        itemRepository.deleteItemById(itemId);
    }

    public ItemDto getItemById(long itemId) {
        throwIfItemNotExist(itemId);
        return ItemMapper.toDto(itemRepository.findItemById(itemId));
    }

    public List<ItemDto> getItemsByUserId(long userId) {
        userService.throwIfUserNotExist(userId);
        return itemRepository.findItemsByUserId(userId).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ItemDto> searchItemsByText(String text) {
        if (text.isEmpty())
            return List.of();
        return itemRepository.findItemsByText(text).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    private Item createItemToUpdate(long id, ItemDto itemDto) {
        Item itemToUpdate = ItemMapper.toModel(itemDto);
        Item oldItem = itemRepository.findItemById(id);
        if (itemToUpdate.getName() == null)
            itemToUpdate.setName(oldItem.getName());
        if (itemToUpdate.getDescription() == null)
            itemToUpdate.setDescription(oldItem.getDescription());
        if (itemToUpdate.getAvailable() == null)
            itemToUpdate.setAvailable(oldItem.getAvailable());
        itemToUpdate.setId(id);
        itemToUpdate.setOwnerId(oldItem.getOwnerId());
        return itemToUpdate;
    }

    private void throwIfItemNotExist(long id) {
        if (!itemRepository.checkItemExist(id))
            throw new NotExistException("Вещи с id " + id + " не существует в системе");
    }
}

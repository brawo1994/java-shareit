package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeption.BadRequestException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto createItem(long userId, ItemDto itemDto) {
        userService.getUserIfExistOrThrow(userId);
        Item item = ItemMapper.toModel(itemDto);
        item.setOwnerId(userId);
        item = itemRepository.save(item);
        log.info("Item with id: {} added to DB", item.getId());
        return ItemMapper.toDto(item);
    }

    @Override
    @Transactional
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        userService.getUserIfExistOrThrow(userId);
        throwIfItemNotOwnedUser(itemId, userId);
        Item updatedItem = itemRepository.save(createItemToUpdate(itemId, itemDto));
        log.info("Item with id: {} updated in DB", userId);
        return ItemMapper.toDto(updatedItem);
    }

    @Override
    @Transactional
    public void deleteItem(long userId, long itemId) {
        userService.getUserIfExistOrThrow(userId);
        getItemIfExistOrThrow(itemId);
        throwIfItemNotOwnedUser(itemId, userId);
        itemRepository.deleteById(itemId);
        log.info("Item with id: {} deleted from DB", itemId);
    }

    @Override
    public ItemDto getItemById(long itemId, long userId) {
        Item item = getItemIfExistOrThrow(itemId);
        ItemDto itemDto = ItemMapper.toDto(item);
        if (item.getOwnerId() == userId) {
            addBookingToItemDto(itemDto);
        }
        addCommentsToItemDto(itemDto);
        return itemDto;
    }

    @Override
    public List<ItemDto> getItemsByUserId(long userId) {
        userService.getUserIfExistOrThrow(userId);
        List<ItemDto> items = itemRepository.findItemsByOwnerId(userId).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
        for (ItemDto itemDto : items) {
            addBookingToItemDto(itemDto);
            addCommentsToItemDto(itemDto);
        }
        return items;
    }

    @Override
    public List<ItemDto> searchItemsByText(String text) {
        if (text.isEmpty())
            return List.of();
        return itemRepository.findItemsByText(text.toLowerCase()).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Item getItemIfExistOrThrow(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> {
            throw new NotFoundException("???????? ?? id " + itemId + " ???? ???????????????????? ?? ??????????????");
        });
    }

    @Override
    public CommentDto createComment(long userId, long itemId, CommentDto commentDto) {
        User user = userService.getUserIfExistOrThrow(userId);
        Item item = getItemIfExistOrThrow(itemId);
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndItemId(userId, itemId, LocalDateTime.now());
        if (bookings.isEmpty())
            throw new BadRequestException("?? ?????? ?????? ???? ???????????? ???????????????????????? ????????????????????????");
        Comment comment = CommentMapper.toModel(commentDto);
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.toDto(commentRepository.save(comment));
    }

    private Item createItemToUpdate(long id, ItemDto itemDto) {
        Item itemToUpdate = ItemMapper.toModel(itemDto);
        Item oldItem = getItemIfExistOrThrow(id);
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

    private void throwIfItemNotOwnedUser(long itemId, long userId) {
        if (getItemIfExistOrThrow(itemId).getOwnerId() != userId)
            throw new NotFoundException("???????? ?? id " + itemId + " ???? ?????????????? ?? ???????????????????????? ?? id " + userId);
    }

    private void addBookingToItemDto(ItemDto itemDto) {
        Optional<Booking> nextBooking = bookingRepository.findNextBooking(itemDto.getId(), LocalDateTime.now());
        nextBooking.ifPresent(booking -> itemDto.setNextBooking(BookingMapper.toShortDto(booking)));
        Optional<Booking> lastBooking = bookingRepository.findLastBooking(itemDto.getId(), LocalDateTime.now());
        lastBooking.ifPresent(booking -> itemDto.setLastBooking(BookingMapper.toShortDto(booking)));
    }

    private void addCommentsToItemDto(ItemDto itemDto) {
        List<CommentDto> commentsDto = commentRepository.findAllByItemId(itemDto.getId()).stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
        itemDto.setComments(commentsDto);
    }
}

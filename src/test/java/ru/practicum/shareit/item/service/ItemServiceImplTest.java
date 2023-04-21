package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingShortDto;
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
import ru.practicum.shareit.item.service.impl.ItemServiceImpl;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.Pagination;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private Request request;
    private Item item;
    private Booking booking;
    private BookingShortDto bookingShortDto;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .id(1L)
                .name("Ivan")
                .email("ivan@ya.ru")
                .build();

        request = Request.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .description("Description")
                .owner(user)
                .build();

        item = Item.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .request(request)
                .available(true)
                .owner(user)
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .item(item)
                .booker(user)
                .build();

        bookingShortDto = BookingMapper.toShortDto(booking);
    }

    @Test
    void createItemTest() {
        when(userService.getUserIfExistOrThrow(anyLong())).thenReturn(user);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(requestRepository.findById(anyLong())).thenReturn(Optional.ofNullable(request));
        ItemDto itemDto = itemService.createItem(user.getId(), ItemMapper.toDto(item));

        assertEquals(item.getId(), itemDto.getId());
    }

    @Test
    void createItemIfRequestNotExistTest() {
        when(userService.getUserIfExistOrThrow(anyLong())).thenReturn(user);
        when(requestRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.createItem(user.getId(), ItemMapper.toDto(item))
        );

        assertEquals("Запрос с id " + request.getId() + " не существует в системе", exception.getMessage());
    }

    @Test
    void updateItemTest() {
        when(userService.getUserIfExistOrThrow(anyLong())).thenReturn(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        ItemDto itemDto = itemService.updateItem(user.getId(), item.getId(), ItemMapper.toDto(item));

        assertEquals(item.getId(), itemDto.getId());
    }

    @Test
    void updateItemWithoutNameAndDescriptionTest() {
        item.setName(null);
        item.setDescription(null);
        when(userService.getUserIfExistOrThrow(anyLong())).thenReturn(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        ItemDto itemDto = itemService.updateItem(user.getId(), item.getId(), ItemMapper.toDto(item));

        assertEquals(item.getId(), itemDto.getId());
    }

    @Test
    void updateItemWithoutAvailableTest() {
        item.setAvailable(null);
        when(userService.getUserIfExistOrThrow(anyLong())).thenReturn(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        ItemDto itemDto = itemService.updateItem(user.getId(), item.getId(), ItemMapper.toDto(item));

        assertEquals(item.getId(), itemDto.getId());
    }

    @Test
    void updateItemWhenItemNotOwnedUserTest() {
        when(userService.getUserIfExistOrThrow(anyLong())).thenReturn(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.updateItem(10L, item.getId(), ItemMapper.toDto(item))
        );

        assertEquals("Вещь с id " + item.getId() + " не найдена у пользователя с id 10", exception.getMessage());
    }

    @Test
    void updateItemWithoutItemTest() {
        when(userService.getUserIfExistOrThrow(anyLong())).thenReturn(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.updateItem(user.getId(), item.getId(), ItemMapper.toDto(item))
        );

        assertEquals("Вещь с id " + item.getId() + " не существует в системе", exception.getMessage());
    }

    @Test
    void deleteItemTest() {
        when(userService.getUserIfExistOrThrow(anyLong())).thenReturn(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        itemService.deleteItem(user.getId(), item.getId());

        verify(itemRepository, times(1)).findById(item.getId());
        verify(itemRepository, times(1)).deleteById(item.getId());
    }


    @Test
    void getItemByIdTest() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(Collections.emptyList());
        when(bookingRepository.findNextBooking(anyLong(), any())).thenReturn(Optional.of(booking));
        when(bookingRepository.findLastBooking(anyLong(), any())).thenReturn(Optional.of(booking));
        ItemDto itemDto = itemService.getItemById(item.getId(), user.getId());

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(bookingShortDto, itemDto.getLastBooking());
        assertEquals(bookingShortDto, itemDto.getNextBooking());
    }

    @Test
    void getItemsByUserIdTest() {
        when(userService.getUserIfExistOrThrow(anyLong())).thenReturn(user);
        when(itemRepository.findItemsByOwnerId(anyLong(), any(Pageable.class))).thenReturn(List.of(item));
        when(bookingRepository.findNextBooking(anyLong(), any())).thenReturn(Optional.of(booking));
        when(bookingRepository.findLastBooking(anyLong(), any())).thenReturn(Optional.of(booking));
        List<ItemDto> itemDtoList = itemService.getItemsByUserId(user.getId(), new Pagination(0, 10, Sort.unsorted()));

        assertEquals(1, itemDtoList.size());
        assertEquals(item.getId(), itemDtoList.get(0).getId());
        assertEquals(bookingShortDto, itemDtoList.get(0).getLastBooking());
        assertEquals(bookingShortDto, itemDtoList.get(0).getNextBooking());
    }

    @Test
    void searchItemsByTextTest() {
        when(itemRepository.findItemsByText(anyString(), any(Pageable.class))).thenReturn(List.of(item));
        List<ItemDto> itemDtoList = itemService.searchItemsByText("text", new Pagination(0, 10, Sort.unsorted()));

        assertEquals(1, itemDtoList.size());
        assertEquals(item.getId(), itemDtoList.get(0).getId());
    }

    @Test
    void searchItemsByTextEmptyTest() {
        List<ItemDto> itemDtoList = itemService.searchItemsByText("", new Pagination(0, 10, Sort.unsorted()));

        assertEquals(0, itemDtoList.size());
    }

    @Test
    void createCommentTest() {
        Comment comment = Comment.builder()
                .id(1L)
                .text("text")
                .item(item)
                .author(user)
                .build();
        when(userService.getUserIfExistOrThrow(anyLong())).thenReturn(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(bookingRepository.findAllByBookerIdAndItemId(anyLong(), anyLong(), any())).thenReturn(List.of(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        CommentDto commentDto = itemService.createComment(user.getId(), item.getId(), CommentMapper.toDto(comment));

        assertEquals(comment.getId(), commentDto.getId());
    }

    @Test
    void createCommentWithoutBookingsTest() {
        Comment comment = Comment.builder()
                .id(1L)
                .text("text")
                .item(item)
                .author(user)
                .build();
        when(userService.getUserIfExistOrThrow(anyLong())).thenReturn(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(bookingRepository.findAllByBookerIdAndItemId(anyLong(), anyLong(), any())).thenReturn(Collections.emptyList());

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> itemService.createComment(user.getId(), item.getId(), CommentMapper.toDto(comment))
        );

        assertEquals("У вас нет ни одного завершенного бронирования", exception.getMessage());
    }
}

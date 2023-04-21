package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.impl.RequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.Pagination;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private RequestServiceImpl requestService;

    private User user;
    private Request request;
    private Item item;

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
    }

    @Test
    void createRequestTest() {
        when(userService.getUserIfExistOrThrow(anyLong())).thenReturn(user);
        when(requestRepository.save(any(Request.class))).thenReturn(request);
        RequestDto requestDto = requestService.createRequest(user.getId(), RequestMapper.toDto(request));

        assertEquals(request.getId(), requestDto.getId());
    }

    @Test
    void getRequestListByOwnerIdTest() {
        when(userService.getUserIfExistOrThrow(anyLong())).thenReturn(user);
        when(requestRepository.findAllByOwnerId(anyLong())).thenReturn(List.of(request));
        when(itemRepository.findAllByRequestOwnerId(anyLong())).thenReturn(List.of(item));
        List<RequestDto> requestDtoList = requestService.getRequestListByOwnerId(user.getId());

        assertEquals(request.getId(), requestDtoList.get(0).getId());
        assertEquals(item.getId(), requestDtoList.get(0).getItems().get(0).getId());
    }

    @Test
    void getAllRequestListTest() {
        when(userService.getUserIfExistOrThrow(anyLong())).thenReturn(user);
        when(requestRepository.findAllWhereOwnerNotCurrentUserByPageable(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(request));
        when(itemRepository.findAllByRequestIds(anyList())).thenReturn(List.of(item));
        List<RequestDto> requestDtoList = requestService.getAllRequestList(user.getId(), new Pagination(0, 10, Sort.unsorted()));

        assertEquals(1, requestDtoList.size());
        assertEquals(request.getId(), requestDtoList.get(0).getId());
        assertEquals(item.getId(), requestDtoList.get(0).getItems().get(0).getId());
    }


    @Test
    void getRequestByIdTest() {
        when(userService.getUserIfExistOrThrow(anyLong())).thenReturn(user);
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(List.of(item));
        when(requestRepository.findById(anyLong())).thenReturn(Optional.ofNullable(request));
        RequestDto requestDto = requestService.getRequestById(user.getId(), request.getId());

        assertEquals(request.getId(), requestDto.getId());
        assertEquals(item.getId(), requestDto.getItems().get(0).getId());
    }

    @Test
    void getRequestByIdWithoutRequestTest() {
        when(userService.getUserIfExistOrThrow(anyLong())).thenReturn(user);
        when(requestRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> requestService.getRequestById(user.getId(), request.getId())
        );

        assertEquals("Запрос с id " + request.getId() + " не существует в системе", exception.getMessage());
    }
}

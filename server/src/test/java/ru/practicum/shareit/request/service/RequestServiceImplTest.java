package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.AssertionErrors;
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
    void testCreateRequest() {
        when(userService.getUserIfExistOrThrow(anyLong())).thenReturn(user);
        when(requestRepository.save(any(Request.class))).thenReturn(request);
        RequestDto requestDto = requestService.createRequest(user.getId(), RequestMapper.toDto(request));

        AssertionErrors.assertEquals("There should have been " + request.getId(),
                request.getId(), requestDto.getId());
    }

    @Test
    void testGetRequestListByOwnerId() {
        when(userService.getUserIfExistOrThrow(anyLong())).thenReturn(user);
        when(requestRepository.findAllByOwnerId(anyLong())).thenReturn(List.of(request));
        when(itemRepository.findAllByRequestOwnerId(anyLong())).thenReturn(List.of(item));
        List<RequestDto> requestDtoList = requestService.getRequestListByOwnerId(user.getId());

        AssertionErrors.assertEquals("There should have been " + request.getId(),
                request.getId(), requestDtoList.get(0).getId());
        AssertionErrors.assertEquals("There should have been " + item.getId(),
                item.getId(), requestDtoList.get(0).getItems().get(0).getId());
    }

    @Test
    void testGetAllRequestList() {
        when(userService.getUserIfExistOrThrow(anyLong())).thenReturn(user);
        when(requestRepository.findAllWhereOwnerNotCurrentUserByPageable(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(request));
        when(itemRepository.findAllByRequestIds(anyList())).thenReturn(List.of(item));
        List<RequestDto> requestDtoList = requestService.getAllRequestList(user.getId(),
                new Pagination(0, 10, Sort.unsorted()));

        AssertionErrors.assertEquals("There should have been 1 Request in the list", 1, requestDtoList.size());
        AssertionErrors.assertEquals("There should have been " + request.getId(),
                request.getId(), requestDtoList.get(0).getId());
        AssertionErrors.assertEquals("There should have been " + item.getId(),
                item.getId(), requestDtoList.get(0).getItems().get(0).getId());
    }


    @Test
    void testGetRequestById() {
        when(userService.getUserIfExistOrThrow(anyLong())).thenReturn(user);
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(List.of(item));
        when(requestRepository.findById(anyLong())).thenReturn(Optional.ofNullable(request));
        RequestDto requestDto = requestService.getRequestById(user.getId(), request.getId());

        AssertionErrors.assertEquals("There should have been " + request.getId(),
                request.getId(), requestDto.getId());
        AssertionErrors.assertEquals("There should have been " + item.getId(),
                item.getId(), requestDto.getItems().get(0).getId());
    }

    @Test
    void testGetRequestByIdWithoutRequest() {
        when(userService.getUserIfExistOrThrow(anyLong())).thenReturn(user);
        when(requestRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> requestService.getRequestById(user.getId(), request.getId())
        );

        AssertionErrors.assertEquals("There should have been: Запрос с id "
                        + request.getId() + " не существует в системе",
                "Запрос с id " + request.getId() + " не существует в системе", exception.getMessage());
    }
}

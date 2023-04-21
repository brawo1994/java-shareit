package ru.practicum.shareit.request.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final UserService userService;
    private final RequestRepository requestRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public RequestDto createRequest(long userId, RequestDto requestDto) {
        User user = userService.getUserIfExistOrThrow(userId);
        Request request = RequestMapper.toModel(requestDto);
        request.setCreated(LocalDateTime.now());
        request.setOwner(user);
        request = requestRepository.save(request);
        log.info("Request with id: {} added to DB", request.getId());
        return RequestMapper.toDto(request);
    }

    @Override
    public List<RequestDto> getRequestListByOwnerId(long userId) {
        userService.getUserIfExistOrThrow(userId);
        List<RequestDto> requests = requestRepository.findAllByOwnerId(userId).stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
        List<ItemDto> items = itemRepository.findAllByRequestOwnerId(userId).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
        return createListOfRequestDto(requests, items);
    }

    @Override
    public List<RequestDto> getAllRequestList(long userId, Pageable pageable) {
        userService.getUserIfExistOrThrow(userId);
        List<RequestDto> requests = requestRepository.findAllWhereOwnerNotCurrentUserByPageable(userId, pageable).stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
        List<ItemDto> itemList = itemRepository.findAllByRequestIds(requests.stream()
                        .map(RequestDto::getId)
                        .collect(Collectors.toList())).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
        return createListOfRequestDto(requests, itemList);
    }

    @Override
    public RequestDto getRequestById(long userId, long requestId) {
        userService.getUserIfExistOrThrow(userId);
        Request request = getRequestIfExistOrThrow(requestId);
        List<ItemDto> items = itemRepository.findAllByRequestId(requestId).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
        RequestDto requestDto = RequestMapper.toDto(request);
        requestDto.setItems(items);
        return requestDto;
    }

    private List<RequestDto> createListOfRequestDto(List<RequestDto> requests, List<ItemDto> items) {
        Map<Long, RequestDto> mapWithRequestsDto = new HashMap<>();
        for (RequestDto request : requests) {
            mapWithRequestsDto.put(request.getId(), request);
        }
        for (ItemDto itemDto : items) {
            if (mapWithRequestsDto.containsKey(itemDto.getRequestId())) {
                mapWithRequestsDto.get(itemDto.getRequestId()).getItems().add(itemDto);
            }
        }
        return new ArrayList<>(mapWithRequestsDto.values());
    }

    private Request getRequestIfExistOrThrow(long requestId) {
        return requestRepository.findById(requestId).orElseThrow(() -> {
            throw new NotFoundException("Запрос с id " + requestId + " не существует в системе");
        });
    }
}

package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RequestServiceImplIntegrationTest {

    private final UserService userService;
    private final RequestService requestService;

    @Test
    void getRequestListByOwnerIdTest() {

        UserDto userDto = UserDto.builder()
                .name("Ivan")
                .email("ivan2@ya.ru")
                .build();
        UserDto resultUserDto = userService.createUser(userDto);

        RequestDto requestDto = RequestDto.builder()
                .description("Description 1")
                .build();
        RequestDto resultRequestDto = requestService.createRequest(resultUserDto.getId(), requestDto);

        RequestDto requestDto2 = RequestDto.builder()
                .description("Description 2")
                .build();
        RequestDto resultRequestDto2 = requestService.createRequest(resultUserDto.getId(), requestDto2);

        List<RequestDto> requestDtoList = requestService.getRequestListByOwnerId(resultUserDto.getId());

        assertEquals(resultRequestDto, requestDtoList.get(0));
        assertEquals(resultRequestDto2, requestDtoList.get(1));
        assertEquals(2, requestDtoList.size());
    }
}

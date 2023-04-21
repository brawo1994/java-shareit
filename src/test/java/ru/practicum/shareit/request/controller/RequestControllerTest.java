package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.service.impl.RequestServiceImpl;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RequestController.class)
@AutoConfigureMockMvc
class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RequestServiceImpl requestService;

    private User user;
    private RequestDto requestDto;
    private static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .id(1L)
                .name("Ivan")
                .email("ivan@ya.ru")
                .build();

        Request request = Request.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .description("Description")
                .owner(user)
                .build();

        requestDto = RequestMapper.toDto(request);
    }

    @Test
    void createRequestTest() throws Exception {
        when(requestService.createRequest(anyLong(), any(RequestDto.class)))
                .thenReturn(requestDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/requests")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID_HEADER, user.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requestDto)));
    }

    @Test
    void getRequestListTest() throws Exception {
        when(requestService.getRequestListByOwnerId(anyLong()))
                .thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests")
                        .header(SHARER_USER_ID_HEADER, user.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(requestDto))));
    }

    @Test
    void getAllRequestListTest() throws Exception {
        when(requestService.getAllRequestList(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests/all")
                        .header(SHARER_USER_ID_HEADER, user.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(requestDto))));
    }

    @Test
    void getRequestTest() throws Exception {
        when(requestService.getRequestById(anyLong(), anyLong()))
                .thenReturn(requestDto);

        mockMvc.perform(get("/requests/1")
                        .header(SHARER_USER_ID_HEADER, user.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requestDto)));
    }
}

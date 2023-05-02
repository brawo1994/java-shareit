package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.request.client.RequestClient;
import ru.practicum.shareit.request.dto.RequestGateDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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
    private RequestClient requestClient;

    private RequestGateDto requestGateDto;
    private static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";

    @BeforeEach
    void beforeEach() {
        requestGateDto = RequestGateDto.builder()
                .description("Request Description")
                .build();
    }

    @Test
    void testCreateRequest() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.OK)
                .body(requestGateDto);

        when(requestClient.createRequest(anyLong(), any(RequestGateDto.class)))
                .thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/requests")
                        .content(objectMapper.writeValueAsString(requestGateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requestGateDto)));
    }

    @Test
    void testGetRequestList() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.OK)
                .body(List.of(requestGateDto));

        when(requestClient.getRequestList(anyLong()))
                .thenReturn(response);

        mockMvc.perform(get("/requests")
                        .header(SHARER_USER_ID_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(requestGateDto))));
    }

    @Test
    void testGetAllRequestList() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.OK)
                .body(List.of(requestGateDto));

        when(requestClient.getAllRequestList(anyLong(), anyInt(), anyInt()))
                .thenReturn(response);

        mockMvc.perform(get("/requests/all")
                        .header(SHARER_USER_ID_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(requestGateDto))));
    }

    @Test
    void testGetRequest() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.OK)
                .body(requestGateDto);

        when(requestClient.getRequest(anyLong(), anyLong()))
                .thenReturn(response);

        mockMvc.perform(get("/requests/1")
                        .header(SHARER_USER_ID_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requestGateDto)));
    }
}
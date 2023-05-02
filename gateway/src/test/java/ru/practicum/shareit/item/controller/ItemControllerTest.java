package ru.practicum.shareit.item.controller;

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
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentGateDto;
import ru.practicum.shareit.item.dto.ItemGateDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;
    private ItemGateDto itemGateDto;
    private CommentGateDto commentGateDto;
    private static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";

    @BeforeEach
    void beforeEach() {
        itemGateDto = ItemGateDto.builder()
                .name("Item")
                .description("Description")
                .available(true)
                .build();

        commentGateDto = CommentGateDto.builder()
                .text("Comment")
                .authorName("User Name")
                .build();
    }

    @Test
    void testCreateItem() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.OK)
                .body(itemGateDto);

        when(itemClient.createItem(anyLong(), any(ItemGateDto.class)))
                .thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                        .content(objectMapper.writeValueAsString(itemGateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemGateDto)));
    }

    @Test
    void testUpdateItem() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.OK)
                .body(itemGateDto);

        when(itemClient.updateItem(anyLong(), anyLong(), any(ItemGateDto.class)))
                .thenReturn(response);

        mockMvc.perform(patch("/items/1")
                        .content(objectMapper.writeValueAsString(itemGateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemGateDto)));
    }

    @Test
    void testDeleteItem() throws Exception {
        mockMvc.perform(delete("/items/1")
                        .header(SHARER_USER_ID_HEADER, 1))
                .andExpect(status().isOk());
    }

    @Test
    void testGetItem() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.OK)
                .body(itemGateDto);

        when(itemClient.getItem(anyLong(), anyLong()))
                .thenReturn(response);

        mockMvc.perform(get("/items/1")
                        .header(SHARER_USER_ID_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemGateDto)));
    }

    @Test
    void testGetItemsByUserId() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.OK)
                .body(List.of(itemGateDto));

        when(itemClient.getItemsByUserId(anyLong(), anyInt(), anyInt()))
                .thenReturn(response);

        mockMvc.perform(get("/items")
                        .header(SHARER_USER_ID_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(itemGateDto))));
    }

    @Test
    void testSearchItems() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.OK)
                .body(List.of(itemGateDto));

        when(itemClient.searchItems(anyString(), anyInt(), anyInt()))
                .thenReturn(response);

        mockMvc.perform(get("/items/search")
                        .param("text", "text"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(itemGateDto))));
    }

    @Test
    void testCreateComment() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.OK)
                .body(commentGateDto);

        when(itemClient.createComment(anyLong(), anyLong(), any(CommentGateDto.class)))
                .thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/items/1/comment")
                        .content(objectMapper.writeValueAsString(commentGateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(commentGateDto)));
    }
}
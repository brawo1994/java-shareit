package ru.practicum.shareit.booking.controller;

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
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingGateDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingClient bookingClient;
    private BookingGateDto bookingGateDto;
    private static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";

    @BeforeEach
    void beforeEach() {
        bookingGateDto = BookingGateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();
    }

    @Test
    void testCreateBooking() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.OK)
                .body(bookingGateDto);

        when(bookingClient.createBooking(anyLong(), any(BookingGateDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingGateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingGateDto)));
    }

    @Test
    void testApprovedBooking() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.OK)
                .body(bookingGateDto);

        when(bookingClient.approvedBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(response);

        mockMvc.perform(patch("/bookings/1")
                        .content(objectMapper.writeValueAsString(bookingGateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID_HEADER, 1)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingGateDto)));
    }

    @Test
    void testGetBookingById() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.OK)
                .body(bookingGateDto);

        when(bookingClient.getBookingById(anyLong(), anyLong()))
                .thenReturn(response);

        mockMvc.perform(get("/bookings/1")
                        .header(SHARER_USER_ID_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingGateDto)));
    }

    @Test
    void testGetBookingsByBookerId() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.OK)
                .body(List.of(bookingGateDto));

        when(bookingClient.getBookingsByBookerId(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(response);

        mockMvc.perform(get("/bookings")
                        .header(SHARER_USER_ID_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(bookingGateDto))));
    }

    @Test
    void testGetBookingsByItemsOwnerId() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.OK)
                .body(List.of(bookingGateDto));

        when(bookingClient.getBookingsByItemsOwnerId(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(response);

        mockMvc.perform(get("/bookings/owner")
                        .header(SHARER_USER_ID_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(bookingGateDto))));
    }
}

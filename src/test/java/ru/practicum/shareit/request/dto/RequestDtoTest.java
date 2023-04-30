package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class RequestDtoTest {

    @Autowired
    private JacksonTester<RequestDto> json;

    private RequestDto requestDto;

    @BeforeEach
    void beforeEach() {
        User user = User.builder()
                .id(1L)
                .name("Ivan")
                .email("ivan@ya.ru")
                .build();

        Request request = Request.builder()
                .id(1L)
                .description("Request description")
                .created(LocalDateTime.now())
                .owner(user)
                .build();

        requestDto = RequestMapper.toDto(request);
    }

    @Test
    void testUserDto() throws Exception {
        JsonContent<RequestDto> result = json.write(requestDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo((int)requestDto.getId());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(requestDto.getDescription());
        assertThat(result).extractingJsonPathArrayValue("$.items").isEmpty();
    }
}

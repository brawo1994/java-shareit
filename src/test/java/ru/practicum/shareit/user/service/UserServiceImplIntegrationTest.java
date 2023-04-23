package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplIntegrationTest {
    private final UserService userService;

    @Test
    void testCreateUser() {

        UserDto userDto = UserDto.builder()
                .name("Ivan")
                .email("ivan1@ya.ru")
                .build();

        UserDto result1 = userService.createUser(userDto);

        assertEquals(userDto.getEmail(), result1.getEmail());
        assertEquals(userDto.getName(), result1.getName());
    }
}

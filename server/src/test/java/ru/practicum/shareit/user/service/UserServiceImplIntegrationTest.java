package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.AssertionErrors;
import ru.practicum.shareit.user.dto.UserDto;

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

        AssertionErrors.assertEquals("There should have been " + userDto.getName(), result1.getName(),
                userDto.getName());
        AssertionErrors.assertEquals("There should have been " + userDto.getEmail(), result1.getEmail(),
                userDto.getEmail());
    }
}

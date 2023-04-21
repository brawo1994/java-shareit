package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.impl.UserServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .id(1L)
                .name("Ivan")
                .email("ivan@ya.ru")
                .build();
    }

    @Test
    void createUserTest() {
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserDto userDto = userService.createUser(UserMapper.toDto(user));

        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }


    @Test
    void updateUserTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserDto userDto = UserMapper.toDto(user);
        userService.updateUser(userDto.getId(), userDto);

        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void updateUserWithoutEmailTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        user.setEmail(null);
        UserDto userDto = UserMapper.toDto(user);
        userService.updateUser(userDto.getId(), userDto);

        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void updateUserWithoutNameTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        user.setName(null);
        UserDto userDto = UserMapper.toDto(user);
        userService.updateUser(userDto.getId(), userDto);

        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void updateUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        UserDto userDto = UserMapper.toDto(user);
        userDto.setId(10L);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.updateUser(user.getId(), userDto)
        );

        assertEquals("Пользователь с id " + user.getId() + " не зарегистрирован в системе", exception.getMessage());
    }

    @Test
    void deleteUserTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        userService.deleteUser(user.getId());

        verify(userRepository, times(1)).findById(user.getId());
        verify(userRepository, times(1)).deleteById(user.getId());
    }

    @Test
    void deleteUserTestNotFound() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        NotFoundException exc = assertThrows(
                NotFoundException.class,
                () -> userService.deleteUser(1L)
        );

        assertEquals("Пользователь с id 1 не зарегистрирован в системе", exc.getMessage());
    }

    @Test
    void getUserByIdTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        UserDto userDto = userService.getUserById(user.getId());

        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void getAllUsersTest() {
        when(userRepository.findAll())
                .thenReturn(List.of(user));
        List<UserDto> userDto = userService.getAllUsers();

        assertEquals(1, userDto.size());
        assertEquals(user.getId(), userDto.get(0).getId());
        assertEquals(user.getName(), userDto.get(0).getName());
        assertEquals(user.getEmail(), userDto.get(0).getEmail());
    }

    @Test
    void getAllUsersWithoutUsersTest() {
        when(userRepository.findAll())
                .thenReturn(Collections.emptyList());
        List<UserDto> userDto = userService.getAllUsers();

        assertEquals(0, userDto.size());
    }
}

package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = userRepository.save(UserMapper.toModel(userDto));
        log.info("User with id: {} added to DB", user.getId());
        return UserMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(long userId, UserDto userDto) {
        User updatedUser = userRepository.save(createUserToUpdate(userId, userDto));
        log.info("User with id: {} updated in DB", userId);
        return UserMapper.toDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(long userId) {
        getUserIfExistOrThrow(userId);
        userRepository.deleteById(userId);
        log.info("User with id: {} deleted from DB", userId);
    }

    @Override
    public UserDto getUserById(long userId) {
        return UserMapper.toDto(getUserIfExistOrThrow(userId));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public User getUserIfExistOrThrow(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Пользователь с id " + userId + " не зарегистрирован в системе");
        });
    }

    private User createUserToUpdate(long userId, UserDto userDto) {
        User userToUpdate = UserMapper.toModel(userDto);
        User oldUser = getUserIfExistOrThrow(userId);
        if (userToUpdate.getEmail() == null) {
            userToUpdate.setEmail(oldUser.getEmail());
        }
        if (userToUpdate.getName() == null)
            userToUpdate.setName(oldUser.getName());
        userToUpdate.setId(userId);
        return userToUpdate;
    }
}

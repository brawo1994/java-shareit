package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.NotExistException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toModel(userDto);
        throwIfEmailExist(user.getEmail());
        return UserMapper.toDto(userRepository.createUser(user));
    }

    public UserDto updateUser(long userId, UserDto userDto) {
        throwIfUserNotExist(userId);
        return UserMapper.toDto(userRepository.updateUser(createUserToUpdate(userId, userDto)));
    }

    public void deleteUser(long userId) {
        throwIfUserNotExist(userId);
        for (Item item : itemRepository.findItemsByUserId(userId)) {
            itemRepository.deleteItemById(item.getId());
        }
        userRepository.deleteUserById(userId);
    }

    public UserDto getUserById(long userId) {
        throwIfUserNotExist(userId);
        return UserMapper.toDto(userRepository.findItemById(userId));
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    public void throwIfUserNotExist(long userId) {
        if (!userRepository.checkUserExist(userId))
            throw new NotExistException("Пользователь с id " + userId + " не зарегистрирован в системе");
    }

    private void throwIfEmailExist(String email) {
        String newUserEmail = email.toLowerCase();
        for (User existingUser : userRepository.findAll()) {
            if (newUserEmail.equals(existingUser.getEmail().toLowerCase())) {
                throw new ValidationException("Пользователь с email " + newUserEmail + " уже зарегистрирован в системе");
            }
        }
    }
    private User createUserToUpdate(long userId, UserDto userDto) {
        User userToUpdate = UserMapper.toModel(userDto);
        User oldUser = userRepository.findItemById(userId);
        if (userToUpdate.getEmail() == null) {
            userToUpdate.setEmail(oldUser.getEmail());
        } else {
            throwIfEmailExist(userToUpdate.getEmail());
        }
        if (userToUpdate.getName() == null)
            userToUpdate.setName(oldUser.getName());
        userToUpdate.setId(userId);
        return userToUpdate;
    }
}

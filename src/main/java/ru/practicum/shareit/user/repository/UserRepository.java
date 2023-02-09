package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {

    User createUser(User user);

    User updateUser(User user);

    void deleteUserById(long userId);

    User findItemById(long userId);

    List<User> findAll();

    boolean checkUserExist(long userId);
}

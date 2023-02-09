package ru.practicum.shareit.user.repository.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;

@Slf4j
@Component
@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private long lastUserId;

    @Override
    public User createUser(User user) {
        long userId = ++lastUserId;
        user.setId(userId);
        users.put(userId,user);
        log.info("User with id: {} added to InMemoryStorage", user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        log.info("User with id: {} edited", user.getId());
        return user;
    }

    @Override
    public void deleteUserById(long userId) {
        users.remove(userId);
        log.info("User with id: {} deleted", userId);
    }

    @Override
    public User findItemById(long userId) {
        return users.get(userId);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public boolean checkUserExist(long userId) {
        return users.containsKey(userId);
    }
}

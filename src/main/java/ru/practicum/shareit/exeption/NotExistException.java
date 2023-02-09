package ru.practicum.shareit.exeption;

public class NotExistException extends RuntimeException {
    public NotExistException(String message) {
        super(message);
    }
}

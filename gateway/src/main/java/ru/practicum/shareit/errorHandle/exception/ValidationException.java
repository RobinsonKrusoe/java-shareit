package ru.practicum.shareit.errorHandle.exception;

//непроверяемое исключение
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}

package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

/**
 * Интерфейс для работы с пользователями
 */
public interface UserService {
    /**
     * Добавление нового пользователя
     */
    UserDto add(UserDto user);

    /**
     * Получение пользователя п идентификатору
     */
    UserDto getDto(long id);
    User getUser(long id);

    /**
     * Получение всех пользователей
     */
    Collection<UserDto> getAll();

    /**
     * Обновление пользователя
     */
    UserDto patch(UserDto user);

    /**
     * Удаление пользователя
     */
    void del(long id);
}

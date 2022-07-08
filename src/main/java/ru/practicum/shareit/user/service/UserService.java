package ru.practicum.shareit.user.service;

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
    UserDto get(long id);

    /**
     * Получение всех пользователей
     */
    Collection<UserDto> getAll();

    /**
     * Обновление пользователя
     */
    UserDto upd(UserDto user);

    /**
     * Удаление пользователя
     */
    void del(long id);
}

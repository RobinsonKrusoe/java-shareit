package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

/**
 * Интерфейс для работы с вещами
 */
public interface ItemService {
    /**
     * Добавление новой вещи
     * @param item вещь
     */
    ItemDto add(ItemDto item, long userId);

    /**
     * Получение вещи по идентификатору
     * @param id идентификатор вещи
     * @return вещь
     */
    ItemDto get(long id);

    /**
     * Получение всех вещей пользователя
     * @param userId пользоаптнль
     * @return вещи
     */
    Collection<ItemDto> getAllUserItems(long userId);

    /**
     * Поиск вещи по тексту в названии или описании
     * @param text строка поиска
     * @return найденные вещи
     */
    Collection<ItemDto> searchItems(String text);

    /**
     * Обновление вещи
     * @param item вещь
     */
    ItemDto patch(ItemDto item, Long itemId, Long userId);

    /**
     * Удаление вещи
     * @param id идентификатор вещи
     */
    void del(long id, long userId);
}

package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

/**
 * Интерфейс для работы с вещами
 */
public interface ItemService {
    /**
     * Добавление новой вещи
     * @param item вещь
     */
    ItemDto add(ItemDto item, Long userId);

    /**
     * Получение вещи по идентификатору
     * @param id идентификатор вещи
     * @return вещь
     */
    ItemDto getDto(Long id, Long userId);
    Item getItem(Long id);

    /**
     * Получение всех вещей пользователя
     * @param userId пользоаптнль
     * @return вещи
     */
    Collection<ItemDto> getAllUserItems(Long userId);

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
     * @param itemId идентификатор вещи
     */
    void del(Long itemId, Long userId);
}

package ru.practicum.shareit.requests.service;

import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    /**
     * Добавить новый запрос вещи.
     * @param itemRequestDto запрос с описанием требующейся вещи
     * @param userId пользователь
     * @return обработанный запрос
     */
    ItemRequestDto add(ItemRequestDto itemRequestDto, Long userId);

    /**
     * Получить данные об одном конкретном запросе
     * @param requestId идентификатор запроса вещи
     * @param userId пользователь
     * @return найденный запрос
     */
    ItemRequestDto get(Long requestId, Long userId);

    ItemRequest getItemRequest(Long requestId);

    /**
     * Получить список своих запросов
     * @param userId пользователь
     * @return список собственных запросов
     */
    List<ItemRequestDto> getOwn(Long userId);

    /**
     * Получить список запросов, созданных другими пользователями
     * @param userId пользователь
     * @return список запросов от других пользователей
     */

    /**
     * Получить список запросов, созданных другими пользователями
     * @param userId пользователь
     * @param from старновая позиция вывода
     * @param size размер вывода
     * @return список запросов от других пользователей
     */
    List<ItemRequestDto> getOthers(Long userId, Integer from, Integer size);

}

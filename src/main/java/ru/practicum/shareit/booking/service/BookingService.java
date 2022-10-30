package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInDto;

import java.util.Collection;

/**
 * Интерфейс для работы с бронированиями
 */
public interface BookingService {
    /**
     * Добавление нового бронирования
     * @param bookingInDto
     * @param userId
     * @return
     */
    BookingDto add(BookingInDto bookingInDto, long userId);

    /**
     * Обновление существующего бронирования
     * @param bookingId
     * @param userId
     * @param approved
     * @return
     */

    BookingDto updateStatus(Long bookingId, Long userId, Boolean approved);

    /**
     * Получение отдельного бронирования
     * @param bookingId
     * @param userId
     * @return
     */
    BookingDto getBooking(Long bookingId, Long userId);

    /**
     * Получение всех бронирований пользователя
     * @param state
     * @param userId
     * @return
     */
    Collection<BookingDto> findUserBookings(String state, long userId, Integer from, Integer size);

    /**
     * Получение бронирований вещей пользователя
     * @param state
     * @param userId
     * @return
     */
    Collection<BookingDto> findOwnerBookings(String state, long userId, Integer from, Integer size);

    /**
     * Проверка арендовал ли человек данную вещь
     * @param itemId
     * @param bookerId
     * @return
     */
    Boolean checkBooker(Long itemId, Long bookerId);
}

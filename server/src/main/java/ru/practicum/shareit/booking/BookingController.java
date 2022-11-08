package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

/**
 * Контроллер для работы с бронированиями
 */
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * Добавление нового запроса на бронирование. Запрос может быть создан любым пользователем, а затем подтверждён
     * владельцем вещи.
     * Эндпоинт — POST /bookings.
     * После создания запрос находится в статусе WAITING — «ожидает подтверждения».
     */
    @PostMapping
    public BookingDto postBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                  @RequestBody BookingInDto bookingInDto) {

        log.info("Creating booking {}, userId={}", bookingInDto, userId);

        return bookingService.add(bookingInDto, userId);
    }

    /**
     * Подтверждение или отклонение запроса на бронирование. Может быть выполнено только владельцем вещи.
     * Затем статус бронирования становится либо APPROVED, либо REJECTED.
     * Эндпоинт — PATCH /bookings/{bookingId}?approved={approved}, параметр approved может принимать
     * значения true или false.
     */
    @PatchMapping("/{bookingId}")
    public BookingDto updateStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @PathVariable Long bookingId,
                                   @RequestParam Boolean approved) {

        log.info("Patch booking {}, userId={}, approved={}", bookingId, userId, approved);

        return bookingService.updateStatus(bookingId, userId, approved);
    }

    /**
     * Получение данных о конкретном бронировании (включая его статус). Может быть выполнено либо автором бронирования,
     * либо владельцем вещи, к которой относится бронирование.
     * Эндпоинт — GET /bookings/{bookingId}.
     */
    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long bookingId) {

        log.info("Get booking {}, userId={}", bookingId, userId);

        return bookingService.getBooking(bookingId, userId);
    }

    /**
     * Получение списка всех бронирований текущего пользователя.
     * Эндпоинт — GET /bookings?state={state}.
     * Параметр state необязательный и по умолчанию равен ALL (англ. «все»).
     * Также он может принимать значения:
     *      CURRENT (англ. «текущие»),
     *      **PAST** (англ. «завершённые»),
     *      FUTURE (англ. «будущие»),
     *      WAITING (англ. «ожидающие подтверждения»),
     *      REJECTED (англ. «отклонённые»).
     * Бронирования должны возвращаться отсортированными по дате от более новых к более старым.
     */
    @GetMapping
    public Collection<BookingDto> findUserBookings(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "0",required = false) Integer from,
            @RequestParam(defaultValue = "10",required = false) Integer size,
            @RequestParam(defaultValue = "ALL", required = false) String state) {

        log.info("Get booking with state {}, userId={}, from={}, size={}", state, userId, from, size);

        return bookingService.findUserBookings(state, userId, from, size);
    }

    /**
     * Получение списка бронирований для всех вещей текущего пользователя.
     * Эндпоинт — GET /bookings/owner?state={state}.
     * Этот запрос имеет смысл для владельца хотя бы одной вещи.
     * Работа параметра state аналогична его работе в предыдущем сценарии.
     */
    @GetMapping("/owner")
    public Collection<BookingDto> findOwnerBookings(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "0",required = false) Integer from,
            @RequestParam(defaultValue = "10",required = false) Integer size,
            @RequestParam(defaultValue = "ALL", required = false) String state) {

        log.info("Get owner bookings with state {}, userId={}, from={}, size={}", state, userId, from, size);

        return bookingService.findOwnerBookings(state, userId, from, size);
    }
}

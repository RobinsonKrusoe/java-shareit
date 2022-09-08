package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.Collection;

/**
 * Контроллер для работы с бронированиями
 */
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
    public BookingDto postBooking(@Valid @RequestBody BookingInDto bookingInDto,
                                  @RequestHeader("X-Sharer-User-Id") long userId){
        return bookingService.add(bookingInDto, userId);
    }

    /**
     * Подтверждение или отклонение запроса на бронирование. Может быть выполнено только владельцем вещи.
     * Затем статус бронирования становится либо APPROVED, либо REJECTED.
     * Эндпоинт — PATCH /bookings/{bookingId}?approved={approved}, параметр approved может принимать
     * значения true или false.
     */
    @PatchMapping("/{bookingId}")
    public BookingDto updateStatus(@PathVariable Long bookingId,
                                   @RequestParam Boolean approved,
                                   @RequestHeader("X-Sharer-User-Id") Long userId){
        return bookingService.updateStatus(bookingId, userId, approved);
    }

    /**
     * Получение данных о конкретном бронировании (включая его статус). Может быть выполнено либо автором бронирования,
     * либо владельцем вещи, к которой относится бронирование.
     * Эндпоинт — GET /bookings/{bookingId}.
     */
    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable Long bookingId,
                                 @RequestHeader("X-Sharer-User-Id") Long userId){
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
    public Collection<BookingDto> findUserBookings(@RequestParam(defaultValue = "ALL", required = false) String state,
                                                   @RequestHeader("X-Sharer-User-Id") long userId){
        return bookingService.findUserBookings(state, userId);
    }


    /**
     * Получение списка бронирований для всех вещей текущего пользователя.
     * Эндпоинт — GET /bookings/owner?state={state}.
     * Этот запрос имеет смысл для владельца хотя бы одной вещи.
     * Работа параметра state аналогична его работе в предыдущем сценарии.
     */
    @GetMapping("/owner")
    public Collection<BookingDto> findOwnerBookings(@RequestParam(defaultValue = "ALL", required = false) String state,
                                                    @RequestHeader("X-Sharer-User-Id") long userId){
        return bookingService.findOwnerBookings(state, userId);
    }
}

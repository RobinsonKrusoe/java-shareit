package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.errorHandle.exception.ValidationException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
	private final BookingClient bookingClient;

	/**
	 * Добавление нового запроса на бронирование. Запрос может быть создан любым пользователем, а затем подтверждён
	 * владельцем вещи.
	 * Эндпоинт — POST /bookings.
	 * После создания запрос находится в статусе WAITING — «ожидает подтверждения».
	 */
	@PostMapping
	public ResponseEntity<Object> postBooking(@RequestHeader("X-Sharer-User-Id") long userId,
											  @Valid @RequestBody BookingInDto bookingInDto) {

		log.info("Creating booking {}, userId={}", bookingInDto, userId);

		return bookingClient.add(bookingInDto, userId);
	}

	/**
	 * Подтверждение или отклонение запроса на бронирование. Может быть выполнено только владельцем вещи.
	 * Затем статус бронирования становится либо APPROVED, либо REJECTED.
	 * Эндпоинт — PATCH /bookings/{bookingId}?approved={approved}, параметр approved может принимать
	 * значения true или false.
	 */
	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> updateStatus(@RequestHeader("X-Sharer-User-Id") long userId,
											   @PathVariable long bookingId,
											   @RequestParam Boolean approved) {

		log.info("Patch booking {}, userId={}, approved={}", bookingId, userId, approved);

		return bookingClient.updateStatus(bookingId, userId, approved);
	}

	/**
	 * Получение данных о конкретном бронировании (включая его статус). Может быть выполнено либо автором бронирования,
	 * либо владельцем вещи, к которой относится бронирование.
	 * Эндпоинт — GET /bookings/{bookingId}.
	 */
	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
											 @PathVariable long bookingId) {

		log.info("Get booking {}, userId={}", bookingId, userId);

		return bookingClient.getBooking(bookingId, userId);
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
	public ResponseEntity<Object> findUserBookings(
			@RequestHeader("X-Sharer-User-Id") long userId,
			@RequestParam(defaultValue = "0",required = false)
			@PositiveOrZero(message = "From должно быть положительным числом или 0") Integer from,
			@RequestParam(defaultValue = "10",required = false)
			@Positive(message = "Size должно быть положительным числом") Integer size,
			@RequestParam(defaultValue = "ALL", required = false) String state) {

		BookingState stateBS = BookingState.from(state)
				.orElseThrow(() -> new ValidationException("Unknown state: " + state));

		log.info("Get booking with state {}, userId={}, from={}, size={}", state, userId, from, size);

		return bookingClient.findUserBookings(userId, stateBS, from, size);
	}

	/**
	 * Получение списка бронирований для всех вещей текущего пользователя.
	 * Эндпоинт — GET /bookings/owner?state={state}.
	 * Этот запрос имеет смысл для владельца хотя бы одной вещи.
	 * Работа параметра state аналогична его работе в предыдущем сценарии.
	 */
	@GetMapping("/owner")
	public ResponseEntity<Object> findOwnerBookings(
			@RequestHeader("X-Sharer-User-Id") long userId,
			@RequestParam(defaultValue = "0",required = false)
			@PositiveOrZero(message = "From должно быть положительным числом или 0") Integer from,
			@RequestParam(defaultValue = "10",required = false)
			@Positive(message = "Size должно быть положительным числом") Integer size,
			@RequestParam(defaultValue = "ALL", required = false) String state) {

		BookingState stateBS = BookingState.from(state)
				.orElseThrow(() -> new ValidationException("Unknown state: " + state));

		log.info("Get owner bookings with state {}, userId={}, from={}, size={}", state, userId, from, size);

		return bookingClient.findOwnerBookings(userId, stateBS, from, size);
	}
}

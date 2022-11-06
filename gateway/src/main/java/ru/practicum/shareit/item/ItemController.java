package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

/**
 * Контроллер для обработки запросов по вещам
 */
@Slf4j
@Validated
@RequestMapping("/items")
@RestController
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    /**
     * Добавление новой вещи
     * На вход поступает объект ItemDto
     * userId в заголовке X-Sharer-User-Id — это идентификатор пользователя, который добавляет вещь.
     * Именно этот пользователь — владелец вещи.
     */
    @PostMapping
    public ResponseEntity<Object> postItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody ItemDto item) {
        log.info("Creating item {}, userId={}", item, userId);
        return itemClient.add(item, userId);
    }

    /**
     * Редактирование вещи
     * Изменить можно название, описание и статус доступа к аренде.
     * Редактировать вещь может только её владелец.
     */
    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> patchItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestBody ItemDto item,
                                            @PathVariable Long itemId) {
        log.info("Patch item {}, itemId={}, userId={}", item, itemId, userId);
        return itemClient.patch(item, itemId, userId);
    }

    /**
     * Просмотр информации о конкретной вещи по её идентификатору.
     * Информацию о вещи может просмотреть любой пользователь.
     */
    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @PathVariable long itemId) {
        log.info("Get itemId={} by userId={}", itemId, userId);
        return itemClient.getDto(itemId, userId);
    }

    /**
     * Просмотр владельцем списка всех его вещей с указанием названия и описания для каждой.
     */
    @GetMapping
    public ResponseEntity<Object> getAllUserItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestParam(defaultValue = "0",required = false) Integer from,
                                                  @RequestParam(defaultValue = "10",required = false) Integer size) {
        log.info("getAllUserItems userId={}, from={}, size={}", userId, from, size);
        return itemClient.getAllUserItems(userId, from, size);
    }

    /**
     * Поиск вещи потенциальным арендатором.
     * Пользователь передаёт в строке запроса текст для поиска,
     * и система ищет вещи, содержащие этот текст в названии или описании.
     */
    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text,
                                              @RequestParam(defaultValue = "0",required = false) Integer from,
                                              @RequestParam(defaultValue = "10",required = false) Integer size) {
        log.info("searchItems text={}, from={}, size={}", text, from, size);
        return itemClient.searchItems(text, from, size);
    }

    /**
     * Удаление вещи
     */
    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable long itemId) {
        log.info("Delete itemId={} by userId={}", itemId, userId);
        itemClient.del(itemId, userId);
    }

    /**
     * Добавление комментария к вещи
     * POST /items/{itemId}/comment
     * @param commentDto
     * @param itemId
     * @param userId
     * @return
     */
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody CommentDto commentDto,
                                           @PathVariable long itemId) {
        log.info("Post commentDto {}, itemId={}, userId={}", commentDto, itemId, userId);
        return itemClient.add(commentDto, itemId, userId);
    }
}

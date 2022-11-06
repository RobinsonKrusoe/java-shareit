package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    /**
     * POST /requests — добавить новый запрос вещи. Основная часть запроса — текст запроса, где пользователь описывает,
     * какая именно вещь ему нужна.
     */
    @PostMapping
    public ResponseEntity<Object> postItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestBody ItemRequestDto itemRequest) {
        log.info("Creating itemRequest {}, userId={}", itemRequest, userId);
        return itemRequestClient.add(itemRequest, userId);
    }

    /**
     * GET /requests — получить список своих запросов вместе с данными об ответах на них. Для каждого запроса должны
     * указываться описание,
     * дата и время создания и
     * список ответов в формате: id вещи, название, id владельца.
     * Так в дальнейшем, используя указанные id вещей, можно будет получить подробную информацию о каждой вещи.
     * Запросы должны возвращаться в отсортированном порядке от более новых к более старым.
     */
     @GetMapping
     public ResponseEntity<Object> getOwnItemRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
         log.info("getOwnItemRequests by userId={}", userId);
         return itemRequestClient.getOwn(userId);
     }

    /**
     * GET /requests/all?from={from}&size={size} — получить список запросов, созданных другими пользователями. С помощью
     * этого эндпоинта пользователи смогут просматривать существующие запросы, на которые они могли бы ответить. Запросы
     * сортируются по дате создания: от более новых к более старым. Результаты должны возвращаться постранично. Для
     * этого нужно передать два параметра: from — индекс первого элемента, начиная с 0, и size — количество элементов
     * для отображения.
     */
    @GetMapping("/all")
    public ResponseEntity<Object> getOthersItemRequests(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "0",required = false) Integer from,
            @RequestParam(defaultValue = "10",required = false) Integer size) {
        log.info("getOthersItemRequests userId={}, from={}, size={}", userId, from, size);
        return itemRequestClient.getOthers(userId, from, size);
    }

     /**
     * GET /requests/{requestId} — получить данные об одном конкретном запросе вместе с данными об ответах на него в том
     * же формате, что и в эндпоинте GET /requests. Посмотреть данные об отдельном запросе может любой пользователь.
     */
    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable long requestId) {
        log.info("Get requestId={} by userId={}", requestId, userId);
        return itemRequestClient.get(requestId, userId);
    }
}

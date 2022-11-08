package ru.practicum.shareit.requests;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.service.ItemRequestService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    /**
     * POST /requests — добавить новый запрос вещи. Основная часть запроса — текст запроса, где пользователь описывает,
     * какая именно вещь ему нужна.
     */
    @PostMapping
    public ItemRequestDto postItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @RequestBody ItemRequestDto itemRequest) {
        log.info("Creating itemRequest {}, userId={}", itemRequest, userId);
        return itemRequestService.add(itemRequest, userId);
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
     public Collection<ItemRequestDto> getOwnItemRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
         log.info("getOwnItemRequests by userId={}", userId);
         return itemRequestService.getOwn(userId);
     }

    /**
     * GET /requests/all?from={from}&size={size} — получить список запросов, созданных другими пользователями. С помощью
     * этого эндпоинта пользователи смогут просматривать существующие запросы, на которые они могли бы ответить. Запросы
     * сортируются по дате создания: от более новых к более старым. Результаты должны возвращаться постранично. Для
     * этого нужно передать два параметра: from — индекс первого элемента, начиная с 0, и size — количество элементов
     * для отображения.
     */
    @GetMapping("/all")
    public Collection<ItemRequestDto> getOthersItemRequests(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "0",required = false) Integer from,
            @RequestParam(defaultValue = "10",required = false) Integer size) {
        log.info("getOthersItemRequests userId={}, from={}, size={}", userId, from, size);
        return itemRequestService.getOthers(userId, from, size);
    }

     /**
     * GET /requests/{requestId} — получить данные об одном конкретном запросе вместе с данными об ответах на него в том
     * же формате, что и в эндпоинте GET /requests. Посмотреть данные об отдельном запросе может любой пользователь.
     */
    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequest(@PathVariable long requestId,
                                         @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get requestId={} by userId={}", requestId, userId);
        return itemRequestService.get(requestId, userId);
    }
}

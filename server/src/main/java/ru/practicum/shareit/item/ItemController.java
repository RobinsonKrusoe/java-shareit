package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

/**
 * Контроллер для обработки запросов по вещам
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final CommentService commentService;

    @Autowired
    public ItemController(ItemService itemService,
                          CommentService commentService) {
        this.itemService = itemService;
        this.commentService = commentService;
    }

    /**
     * Добавление новой вещи
     * На вход поступает объект ItemDto
     * userId в заголовке X-Sharer-User-Id — это идентификатор пользователя, который добавляет вещь.
     * Именно этот пользователь — владелец вещи.
     */
    @PostMapping
    public ItemDto postItem(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody ItemDto item) {
        log.info("Creating item {}, userId={}", item, userId);
        return itemService.add(item, userId);
    }

    /**
     * Редактирование вещи
     * Изменить можно название, описание и статус доступа к аренде.
     * Редактировать вещь может только её владелец.
     */
    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestBody ItemDto item,
                             @PathVariable Long itemId,
                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Patch item {}, itemId={}, userId={}", item, itemId, userId);
        return itemService.patch(item, itemId, userId);
    }

    /**
     * Просмотр информации о конкретной вещи по её идентификатору.
     * Информацию о вещи может просмотреть любой пользователь.
     */
    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId,
                               @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get itemId={} by userId={}", itemId, userId);
        return itemService.getDto(itemId, userId);
    }

    /**
     * Просмотр владельцем списка всех его вещей с указанием названия и описания для каждой.
     */
    @GetMapping
    public Collection<ItemDto> getAllUserItems(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "0",required = false) Integer from,
            @RequestParam(defaultValue = "10",required = false) Integer size) {
        log.info("getAllUserItems userId={}, from={}, size={}", userId, from, size);
        return itemService.getAllUserItems(userId, from, size);
    }

    /**
     * Поиск вещи потенциальным арендатором.
     * Пользователь передаёт в строке запроса текст для поиска,
     * и система ищет вещи, содержащие этот текст в названии или описании.
     */
    @GetMapping("/search")
    public Collection<ItemDto> searchItems(
            @RequestParam(defaultValue = "0",required = false) Integer from,
            @RequestParam(defaultValue = "10",required = false) Integer size,
            @RequestParam String text) {
        log.info("searchItems text={}, from={}, size={}", text, from, size);
        return itemService.searchItems(text, from, size);
    }

    /**
     * Удаление вещи
     */
    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable long itemId) {
        log.info("Delete itemId={} by userId={}", itemId, userId);
        itemService.del(itemId, userId);
    }

    /**
     * Добавление комментария к вещи
     * POST /items/{itemId}/comment
     * @param userId
     * @param commentDto
     * @param itemId
     * @return
     */
    @PostMapping("/{itemId}/comment")
    public CommentDto postItem(@RequestHeader("X-Sharer-User-Id") long userId,
                               @RequestBody CommentDto commentDto,
                               @PathVariable long itemId) {
        log.info("Post commentDto {}, itemId={}, userId={}", commentDto, itemId, userId);
        return commentService.add(commentDto, itemId, userId);
    }
}

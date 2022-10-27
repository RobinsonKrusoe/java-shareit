package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

/**
 * Контроллер для обработки запросов по вещам
 */
@RestController
@Validated
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
    public ItemDto postItem(@Valid @RequestBody ItemDto item,
                         @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.add(item, userId);
    }

    /**
     * Редактирование вещи
     * Изменить можно название, описание и статус доступа к аренде.
     * Редактировать вещь может только её владелец.
     */
    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@Valid @RequestBody ItemDto item,
                             @PathVariable Long itemId,
                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.patch(item, itemId, userId);
    }

    /**
     * Просмотр информации о конкретной вещи по её идентификатору.
     * Информацию о вещи может просмотреть любой пользователь.
     */
    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId,
                               @RequestHeader("X-Sharer-User-Id") long userId) {
        ItemDto ret = itemService.getDto(itemId, userId);
        return ret;
    }

    /**
     * Просмотр владельцем списка всех его вещей с указанием названия и описания для каждой.
     */
    @GetMapping
    public Collection<ItemDto> getAllUserItems(
            @PositiveOrZero(message = "From должно быть положительным числом или 0")
            @RequestParam(defaultValue = "0",required = false) Integer from,
            @Positive(message = "Size должно быть положительным числом")
            @RequestParam(defaultValue = "10",required = false) Integer size,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getAllUserItems(userId, from, size);
    }

    /**
     * Поиск вещи потенциальным арендатором.
     * Пользователь передаёт в строке запроса текст для поиска,
     * и система ищет вещи, содержащие этот текст в названии или описании.
     */
    @GetMapping("/search")
    public Collection<ItemDto> searchItems(
            @PositiveOrZero(message = "From должно быть положительным числом или 0")
            @RequestParam(defaultValue = "0",required = false) Integer from,
            @Positive(message = "Size должно быть положительным числом")
            @RequestParam(defaultValue = "10",required = false) Integer size,
            @RequestParam String text) {
        return itemService.searchItems(text, from, size);
    }

    /**
     * Удаление вещи
     */
    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable long itemId) {
        itemService.del(itemId, userId);
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
    public CommentDto postItem(@Valid @RequestBody CommentDto commentDto,
                               @PathVariable long itemId,
                               @RequestHeader("X-Sharer-User-Id") long userId) {
        return commentService.add(commentDto, itemId, userId);
    }
}

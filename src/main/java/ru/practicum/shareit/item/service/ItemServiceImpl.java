package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.errorHandle.exception.AccessForbiddenException;
import ru.practicum.shareit.errorHandle.exception.EntityNotFoundException;
import ru.practicum.shareit.errorHandle.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.*;

@Component
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private UserService userService;
    private final Map<Long, Item> items = new HashMap<>();
    private long iter = 1;

    @Autowired
    public ItemServiceImpl(UserService userService) {
        this.userService = userService;
    }

    /**
     * Добавление новой вещи
     *
     * @param item вещь
     */
    @Override
    public ItemDto add(ItemDto item, long userId) {
        User user = userService.get(userId);
        if(item.getAvailable() == null){
            throw new ValidationException("Не заполнено поле доступности!");
        }
        if(item.getName() == null || item.getName().isBlank()){
            throw new ValidationException("Не заполнено название вещи!");
        }

        if(item.getDescription() == null || item.getDescription().isBlank()){
            throw new ValidationException("Не заполнено описание вещи!");
        }

        item.setId(iter++);
        Item itemForBase = ItemMapper.toItem(item);
        itemForBase.setOwner(user);
        items.put(itemForBase.getId(), itemForBase);
        return item;
    }

    /**
     * Получение вещи по идентификатору
     *
     * @param id идентификатор вещи
     * @return вещь
     */
    @Override
    public ItemDto get(long id) {
        return ItemMapper.toItemDto(items.get(id));
    }

    /**
     * Получение всех вещей пользователя
     *
     * @param userId пользоаптнль
     * @return вещи
     */
    @Override
    public Collection<ItemDto> getAllUserItems(long userId) {
        List<ItemDto> ret = new ArrayList<>();
        for (Item item : items.values()) {
            if(item.getOwner().getId() == userId){
                ret.add(ItemMapper.toItemDto(item));
            }
        }
        return ret;
    }

    /**
     * Поиск вещи по тексту в названии или описании
     *
     * @param text строка поиска
     * @return найденные вещи
     */
    @Override
    public Collection<ItemDto> searchItems(String text) {
        List<ItemDto> ret = new ArrayList<>();
        if(text != null && !text.isBlank()){
            for (Item item : items.values()) {
                if (item.isAvailable() && (item.getName() != null &&
                                           item.getName().toUpperCase().contains(text.toUpperCase()) ||
                                           item.getDescription() != null &&
                                           item.getDescription().toUpperCase().contains(text.toUpperCase()))) {
                    ret.add(ItemMapper.toItemDto(item));
                }
            }
        }
        return ret;
    }

    /**
     * Обновление вещи
     *
     * @param item вещь
     */
    @Override
    public ItemDto patch(ItemDto item, Long itemId, Long userId) {
        User user = userService.get(userId);

        if(!items.containsKey(itemId)){
            throw new EntityNotFoundException("Вещь с идентификатором" + itemId + " не найдена!");
        }

        Item itemInBase = items.get(itemId);

        if(itemInBase.getOwner().getId() != userId){
            throw new AccessForbiddenException("Можно вносить изменения только в свои вещи!");
        }

        if(item.getName() != null){
            itemInBase.setName(item.getName());
        }

        if(item.getDescription() != null){
            itemInBase.setDescription(item.getDescription());
        }

        if(item.getAvailable() != null){
            itemInBase.setAvailable(item.getAvailable());
        }

        return ItemMapper.toItemDto(itemInBase);
    }

    /**
     * Удаление вещи
     *
     * @param id идентификатор вещи
     */
    @Override
    public void del(long id, long userId) {
        if(!items.containsKey(id)){
            throw new EntityNotFoundException("Вещь с идентификатором" + id + " не найдена!");
        }

        Item itemInBase = items.get(id);

        if(itemInBase.getOwner().getId() != userId){
            throw new AccessForbiddenException("Можно удалять только свои вещи!");
        }

        items.remove(id);
    }
}

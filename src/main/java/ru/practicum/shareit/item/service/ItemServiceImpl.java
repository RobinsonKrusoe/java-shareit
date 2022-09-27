package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.errorHandle.exception.AccessForbiddenException;
import ru.practicum.shareit.errorHandle.exception.EntityNotFoundException;
import ru.practicum.shareit.errorHandle.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.Instant;
import java.util.*;

@Component
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemRepository itemRepository;

    private final CommentService commentService;

    @Autowired
    public ItemServiceImpl(UserService userService,
                           ItemRepository itemRepository,
                           CommentService commentService) {
        this.userService = userService;
        this.itemRepository = itemRepository;
        this.commentService = commentService;
    }

    /**
     * Добавление новой вещи
     *
     * @param itemDto вещь
     */
    @Override
    public ItemDto add(ItemDto itemDto, Long userId) {
        User owner = userService.getUser(userId);
        if(itemDto.getAvailable() == null){
            throw new ValidationException("Не заполнено поле доступности!");
        }
        if(itemDto.getName() == null || itemDto.getName().isBlank()){
            throw new ValidationException("Не заполнено название вещи!");
        }

        if(itemDto.getDescription() == null || itemDto.getDescription().isBlank()){
            throw new ValidationException("Не заполнено описание вещи!");
        }

        Item itemForBase = ItemMapper.toItem(itemDto);
        itemForBase.setOwner(owner);

        itemForBase = itemRepository.saveAndFlush(itemForBase);

        return ItemMapper.toItemDto(itemForBase);
    }

    /**
     * Получение вещи по идентификатору
     *
     * @param itemId идентификатор вещи
     * @return вещь
     */
    @Override
    public ItemDto getDto(Long itemId, Long userId) {
        ItemDto itemDto = ItemMapper.toItemDto(getItem(itemId));
        // Если запрашивает хозяин вещи - добавить информацию о бронированиях
        if (itemDto.getOwner() != null && itemDto.getOwner().getId() == userId){
            itemDto.setLastBooking(getLastBooking(itemId));
            itemDto.setNextBooking(getNextBooking(itemId));
        }

        itemDto.setComments(commentService.findItemComments(itemId));

        return itemDto;
    }

    @Override
    public Item getItem(Long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if(item.isPresent()){
            return item.get();
        }else {
            throw new EntityNotFoundException("Вещь с идентификатором" + itemId + " не найдена!");
        }
    }
    /**
     * Получение всех вещей пользователя
     *
     * @param userId пользоаптнль
     * @return вещи
     */
    @Override
    public Collection<ItemDto> getAllUserItems(Long userId) {
        List<ItemDto> ret = new ArrayList<>();
        ItemDto itemDto = null;
        for (Item item : itemRepository.findItemsByOwnerIdOrderById(userId)) {
            itemDto = ItemMapper.toItemDto(item);
            itemDto.setLastBooking(getLastBooking(item.getId()));
            itemDto.setNextBooking(getNextBooking(item.getId()));
            itemDto.setComments(commentService.findItemComments(item.getId()));

            ret.add(itemDto);
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
            for (Item item : itemRepository.search(text)) {
                ret.add(ItemMapper.toItemDto(item));
            }
        }
        return ret;
    }

    /**
     * Обновление вещи
     *
     * @param itemDto вещь
     */
    @Override
    public ItemDto patch(ItemDto itemDto, Long itemId, Long userId) {
        Item itemInBase = getItem(itemId);

        if(itemInBase.getOwner().getId() != userId){
            throw new AccessForbiddenException("Можно вносить изменения только в свои вещи!");
        }

        if(itemDto.getName() != null){
            itemInBase.setName(itemDto.getName());
        }

        if(itemDto.getDescription() != null){
            itemInBase.setDescription(itemDto.getDescription());
        }

        if(itemDto.getAvailable() != null){
            itemInBase.setAvailable(itemDto.getAvailable());
        }

        itemInBase = itemRepository.saveAndFlush(itemInBase);

        return ItemMapper.toItemDto(itemInBase);
    }

    /**
     * Удаление вещи
     *
     * @param id идентификатор вещи
     */
    @Override
    public void del(Long id, Long userId) {
        Optional<Item> item = itemRepository.findById(id);
        if(!item.isPresent()){
            throw new EntityNotFoundException("Вещь с идентификатором" + id + " не найдена!");
        }

        if(item.get().getOwner().getId() != userId){
            throw new AccessForbiddenException("Можно удалять только свои вещи!");
        }

        itemRepository.deleteById(id);
    }

    private ItemDto.BookingIner getLastBooking(Long itemId){
        Object[] o = itemRepository.findLastBooking(itemId, Date.from(Instant.now())).get(0);
        if(o[0]!= null && o[1]!= null){
            return new ItemDto.BookingIner(Long.valueOf(o[0].toString()), Long.valueOf(o[1].toString()));
        } else {
            return null;
        }
    }

    private ItemDto.BookingIner getNextBooking(Long itemId){
        Object[] o = itemRepository.findNextBooking(itemId, Date.from(Instant.now())).get(0);
        if(o[0]!= null && o[1]!= null){
            return new ItemDto.BookingIner(Long.valueOf(o[0].toString()), Long.valueOf(o[1].toString()));
        } else {
            return null;
        }
    }
}

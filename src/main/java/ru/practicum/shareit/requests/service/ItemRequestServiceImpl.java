package ru.practicum.shareit.requests.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.errorHandle.exception.EntityNotFoundException;
import ru.practicum.shareit.errorHandle.exception.ValidationException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.ItemRequestMapper;
import ru.practicum.shareit.requests.ItemRequestRepository;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.*;

@Component
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository repository;
    private final UserService userService;
    private final ItemService itemService;

    public ItemRequestServiceImpl(ItemRequestRepository repository,
                                  UserService userService,
                                  @Lazy ItemService itemService) {
        this.repository = repository;
        this.userService = userService;
        this.itemService = itemService;
    }

    /**
     * Добавить новый запрос вещи.
     *
     * @param itemRequestDto запрос с описанием требующейся вещи
     * @param userId         пользователь
     * @return обработанный запрос
     */
    @Override
    public ItemRequestDto add(ItemRequestDto itemRequestDto, Long userId) {
        User requestor = userService.getUser(userId);

        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isBlank()) {
            throw new ValidationException("Не заполнено описание вещи!");
        }

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestor(requestor);

        itemRequest = repository.saveAndFlush(itemRequest);

        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    /**
     * Получить данные об одном конкретном запросе.
     * Может посмотреть любой пользователь
     *
     * @param requestId идентификатор запроса вещи
     * @return найденный запрос
     */
    @Override
    public ItemRequestDto get(Long requestId, Long requestorId) {
        User requestor = userService.getUser(requestorId);
        ItemRequest itemRequest = getItemRequest(requestId);
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(itemService.searchItemsByRequest(itemRequest.getId()));
        return itemRequestDto;
    }

    @Override
    public ItemRequest getItemRequest(Long requestId) {
        Optional<ItemRequest> itemRequest = repository.findById(requestId);

        if (itemRequest.isPresent()) {
            return itemRequest.get();
        } else {
            throw new EntityNotFoundException("Запрос с идентификатором " + requestId + " не найден!");
        }
    }

    /**
     * Получить список своих запросов
     *
     * @param requestorId пользователь
     * @return список собственных запросов
     */
    @Override
    public List<ItemRequestDto> getOwn(Long requestorId) {
        User requestor = userService.getUser(requestorId);
        List<ItemRequestDto> ret = new ArrayList<>();
        ItemRequestDto itemRequestDto = null;

        for (ItemRequest request : repository.findAllByRequestor_IdOrderByCreatedDesc(requestorId)) {
            itemRequestDto = ItemRequestMapper.toItemRequestDto(request);
            itemRequestDto.setItems(itemService.searchItemsByRequest(request.getId()));
            ret.add(itemRequestDto);
        }

        return ret;
    }

    /**
     * Получить список запросов, созданных другими пользователями
     * @param userId пользователь
     * @param from старновая позиция вывода
     * @param size размер вывода
     * @return список запросов от других пользователей
     */
    @Override
    public List<ItemRequestDto> getOthers(Long userId, Integer from, Integer size) {
        if (from < 0 || size < 0) {
            throw new ValidationException("Параметры частичной выдачи результата не могут быть отрицательными!");
        }
        Pageable pagingSet = PageRequest.of(from, size);
        List<ItemRequestDto> ret = new ArrayList<>();
        ItemRequestDto itemRequestDto = null;

        for (ItemRequest request : repository.findAllByRequestor_IdNotOrderByCreatedDesc(userId, pagingSet)) {
            itemRequestDto = ItemRequestMapper.toItemRequestDto(request);
            itemRequestDto.setItems(itemService.searchItemsByRequest(request.getId()));
            ret.add(itemRequestDto);
        }

        return ret;
    }
}

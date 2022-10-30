package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.errorHandle.exception.AccessForbiddenException;
import ru.practicum.shareit.errorHandle.exception.EntityNotFoundException;
import ru.practicum.shareit.errorHandle.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.service.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;

class ItemServiceImplTest {
    ItemRepository repository;
    ItemService itemService;
    UserService userService;
    ItemRequestService requestService;
    CommentService commentService;

    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("UserOne")
            .email("UserOne@mail.tst")
            .build();

    private final User userOne = new User(1L, "UserOne", "UserOne@mail.tst");

    private final Item item = new Item(1L,
            "Аккумуляторная дрель",
            "Аккумуляторная дрель + аккумулятор",
            true,
            userOne,
            null);

    private final Item itemTwo = new Item(2L,
            "Отвертка",
            "Аккумуляторная отвертка",
            true,
            userOne,
            null);

    private final ItemRequest itemRequest = new ItemRequest(1L,
            "Хотел бы воспользоваться щёткой для обуви",
            userOne,
            LocalDateTime.now());

    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Аккумуляторная дрель")
            .description("Аккумуляторная дрель + аккумулятор")
            .available(true)
            .owner(userDto)
            .lastBooking(new ItemDto.Booking(1L, 2L))
            .nextBooking(new ItemDto.Booking(1L, 2L))
            .build();

    private final CommentDto commentDto = CommentDto.builder()
            .id(1L)
            .text("Отлично")
            .item(itemDto)
            .authorName("UserTwo")
            .build();

    @BeforeEach
    void beforeEach() {
        repository = mock(ItemRepository.class);
        userService = mock(UserService.class);
        requestService = mock(ItemRequestService.class);
        commentService = mock(CommentService.class);

        Mockito.when(repository.saveAndFlush(any())).then(invocation -> invocation.getArgument(0));
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(item));
        Mockito.when(repository.findById(99L)).thenReturn(Optional.empty());
        Mockito.when(repository.findLastBooking(any(), any()))
                .thenReturn(Collections.singletonList(List.of(1L, 2L).toArray()));
        Mockito.when(repository.findItemsByOwnerIdOrderById(any(), any()))
                .thenReturn(new PageImpl<>(List.of(item, itemTwo)));
        Mockito.when(repository.findNextBooking(any(), any()))
                .thenReturn(Collections.singletonList(List.of(1L, 2L).toArray()));
        Mockito.when(repository.search(any(), any())).thenReturn(new PageImpl<>(List.of(item, itemTwo)));
        Mockito.when(repository.findAllByRequest_Id(any())).thenReturn(List.of(item, itemTwo));

        Mockito.when(userService.getUser(1L)).thenReturn(userOne);
        Mockito.when(requestService.getItemRequest(any())).thenReturn(itemRequest);
        Mockito.when(commentService.findItemComments(any())).thenReturn(List.of(commentDto));

        itemService = new ItemServiceImpl(userService, requestService, repository, commentService);
    }

    @Test
    void add() {
        ItemDto itemAddDto = ItemDto.builder()
                .id(1L)
                .owner(userDto)
                .requestId(1L)
                .build();
        //Запрос без поля доступности должен вызывать исключение
        assertThrows(ValidationException.class, () -> itemService.add(itemAddDto, 1L));
        itemAddDto.setAvailable(true);

        //Запрос без названия вещи должен вызывать исключение
        assertThrows(ValidationException.class, () -> itemService.add(itemAddDto, 1L));
        itemAddDto.setName("Аккумуляторная дрель");

        //Запрос без описания вещи должен вызывать исключение
        assertThrows(ValidationException.class, () -> itemService.add(itemAddDto, 1L));
        itemAddDto.setDescription("Аккумуляторная дрель + аккумулятор");

        assertEquals(itemAddDto, itemService.add(itemAddDto, 1L));
    }

    @Test
    void getDto() {
        ItemDto itemDtoExp = ItemDto.builder()
                .id(1L)
                .name("Аккумуляторная дрель")
                .description("Аккумуляторная дрель + аккумулятор")
                .available(true)
                .owner(userDto)
                .lastBooking(new ItemDto.Booking(1L, 2L))
                .nextBooking(new ItemDto.Booking(1L, 2L))
                .comments(List.of(commentDto))
                .build();

        assertEquals(itemDtoExp, itemService.getDto(1L, 1L));

        //Запрос несуществующей вещи должен вызвать исключение
        assertThrows(EntityNotFoundException.class, () -> itemService.getDto(99L, 1L));
    }

    @Test
    void getAllUserItems() {
        ItemDto itemDtoOne = ItemDto.builder()
                .id(1L)
                .name("Аккумуляторная дрель")
                .description("Аккумуляторная дрель + аккумулятор")
                .available(true)
                .owner(userDto)
                .lastBooking(new ItemDto.Booking(1L, 2L))
                .nextBooking(new ItemDto.Booking(1L, 2L))
                .comments(List.of(commentDto))
                .build();

        ItemDto itemDtoTwo = ItemDto.builder()
                .id(2L)
                .name("Отвертка")
                .description("Аккумуляторная отвертка")
                .available(true)
                .owner(userDto)
                .lastBooking(new ItemDto.Booking(1L, 2L))
                .nextBooking(new ItemDto.Booking(1L, 2L))
                .comments(List.of(commentDto))
                .build();

        //Некорректные параметры порции данных должны вызывать исключение
        assertThrows(IllegalArgumentException.class, () -> itemService.getAllUserItems(1L, -1, 1));
        assertThrows(IllegalArgumentException.class, () -> itemService.getAllUserItems(1L, 0, -1));

        assertEquals(List.of(itemDtoOne, itemDtoTwo), itemService.getAllUserItems(1L, 0, 1));
    }

    @Test
    void searchItems() {
        ItemDto itemDtoOne = ItemDto.builder()
                .id(1L)
                .name("Аккумуляторная дрель")
                .description("Аккумуляторная дрель + аккумулятор")
                .available(true)
                .owner(userDto)
                .build();

        ItemDto itemDtoTwo = ItemDto.builder()
                .id(2L)
                .name("Отвертка")
                .description("Аккумуляторная отвертка")
                .available(true)
                .owner(userDto)
                .build();

        //Некорректные параметры порции данных должны вызывать исключение
        assertThrows(IllegalArgumentException.class, () -> itemService.searchItems("test", -1, 1));
        assertThrows(IllegalArgumentException.class, () -> itemService.searchItems("test", 0, -1));

        assertEquals(List.of(itemDtoOne, itemDtoTwo), itemService.searchItems("test", 0, 1));
    }

    @Test
    void searchItemsByRequest() {
        ItemDto itemDtoOne = ItemDto.builder()
                .id(1L)
                .name("Аккумуляторная дрель")
                .description("Аккумуляторная дрель + аккумулятор")
                .available(true)
                .owner(userDto)
                .build();

        ItemDto itemDtoTwo = ItemDto.builder()
                .id(2L)
                .name("Отвертка")
                .description("Аккумуляторная отвертка")
                .available(true)
                .owner(userDto)
                .build();

        assertEquals(List.of(itemDtoOne, itemDtoTwo), itemService.searchItemsByRequest(1L));
    }

    @Test
    void patch() {
        ItemDto itemDtoOne = ItemDto.builder()
                .id(1L)
                .name("Аккумуляторная дрель")
                .description("Аккумуляторная дрель + аккумулятор")
                .available(true)
                .owner(userDto)
                .build();

        //Обновление несуществующей вещи должно вызывать исключение
        assertThrows(EntityNotFoundException.class, () -> itemService.patch(itemDtoOne, 99L, 1L));

        //Обновление не своей вещи должно вызывать исключение
        assertThrows(AccessForbiddenException.class, () -> itemService.patch(itemDtoOne, 1L, 2L));

        assertEquals(itemDtoOne, itemService.patch(itemDtoOne, 1L, 1L));
    }

    @Test
    void del() {
        //Обновление несуществующей вещи должно вызывать исключение
        assertThrows(EntityNotFoundException.class, () -> itemService.del(99L, 1L));

        //Обновление не своей вещи должно вызывать исключение
        assertThrows(AccessForbiddenException.class, () -> itemService.del(1L, 2L));

        itemService.del(1L, 1L);
        Mockito.verify(repository, Mockito.times(1))
                .deleteById(anyLong());
    }
}

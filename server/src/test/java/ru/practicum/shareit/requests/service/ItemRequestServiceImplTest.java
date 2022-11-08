package ru.practicum.shareit.requests.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.errorHandle.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.ItemRequestRepository;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItemRequestServiceImplTest {

    ItemRequestRepository repository;
    ItemRequestService itemRequestService;
    UserService userService;
    ItemService itemService;

    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("UserOne")
            .email("UserOne@mail.tst")
            .build();

    private final User userOne = new User(1L, "UserOne", "UserOne@mail.tst");
    private final User userTwo = new User(2L, "UserTwo", "UserTwo@mail.tst");

    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Аккумуляторная дрель")
            .description("Аккумуляторная дрель + аккумулятор")
            .available(true)
            .owner(userDto)
            .build();

    private final ItemDto itemDtoTwo = ItemDto.builder()
            .id(2L)
            .name("Отвертка")
            .description("Аккумуляторная отвертка")
            .available(true)
            .owner(userDto)
            .build();

    private final ItemRequest itemRequest = new ItemRequest(1L,
            "Хотел бы воспользоваться щёткой для обуви",
            userOne,
            LocalDateTime.now());

    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(1L)
            .description("Хотел бы воспользоваться щёткой для обуви")
            .requestor(userDto)
            .created(LocalDateTime.now())
            .items(List.of(itemDto, itemDtoTwo))
            .build();

    @BeforeEach
    void beforeEach() {
        repository = mock(ItemRequestRepository.class);
        userService = mock(UserService.class);
        itemService = mock(ItemService.class);

        Mockito.when(repository.saveAndFlush(any())).then(invocation -> invocation.getArgument(0));
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(itemRequest));
        Mockito.when(repository.findById(99L)).thenReturn(Optional.empty());
        Mockito.when(repository.findAllByRequestor_IdOrderByCreatedDesc(1L))
                .thenReturn(List.of(itemRequest));
        when(repository.findAllByRequestor_IdNotOrderByCreatedDesc(any(), any()))
                .thenReturn(new PageImpl<>(List.of(itemRequest)));

        Mockito.when(itemService.searchItemsByRequest(1L)).thenReturn(List.of(itemDto, itemDtoTwo));

        Mockito.when(userService.getUser(1L)).thenReturn(userOne);
        Mockito.when(userService.getUser(2L)).thenReturn(userTwo);

        itemRequestService = new ItemRequestServiceImpl(repository, userService, itemService);
    }

    @Test
    void add() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(0L)
                .build();

        ItemRequestDto itemRequestDtoOut = ItemRequestDto.builder()
                .id(0L)
                .description("Хотел бы воспользоваться щёткой для обуви")
                .requestor(userDto)
                .build();

        //Запрос без описания должен вызывать исключение
//        assertThrows(ValidationException.class, () -> itemRequestService.add(itemRequestDto, 1L));

        itemRequestDto.setDescription("Хотел бы воспользоваться щёткой для обуви");
        assertEquals(itemRequestDtoOut, itemRequestService.add(itemRequestDto, 1L));
    }

    @Test
    void get() {
        //Вызов несуществующего запроса должен вызывать исключение
        assertThrows(EntityNotFoundException.class, () -> itemRequestService.get(99L, 1L));

        assertEquals(itemRequestDto, itemRequestService.get(1L, 1L));
    }

    @Test
    void getOwn() {
        assertEquals(List.of(itemRequestDto), itemRequestService.getOwn(1L));
    }

    @Test
    void getOthers() {
        //Некорректные параметры порции данных должны вызывать исключение
        assertThrows(IllegalArgumentException.class, () -> itemRequestService.getOthers(2L, -1, 1));
        assertThrows(IllegalArgumentException.class, () -> itemRequestService.getOthers(2L, 0, -1));

        assertEquals(List.of(itemRequestDto), itemRequestService.getOthers(2L, 0, 1));
    }
}

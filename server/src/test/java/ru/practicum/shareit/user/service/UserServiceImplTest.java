package ru.practicum.shareit.user.service;

import static org.junit.jupiter.api.Assertions.*;

import ru.practicum.shareit.errorHandle.exception.EntityNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

class UserServiceImplTest {
    UserService userService;
    UserRepository userRepository;

    private final UserDto userDtoOne = UserDto.builder()
            .id(1L)
            .name("UserOne")
            .email("UserOne@mail.tst")
            .build();

    private final UserDto userDtoTwo = UserDto.builder()
            .id(2L)
            .name("UserTwo")
            .email("UserTwo@mail.tst")
            .build();

    private final UserDto userDtoErrUpd = UserDto.builder()
            .id(99L)
            .name("UserUpd")
            .email("UserUpd@mail.tst")
            .build();

    private final User userOne = new User(1L, "UserOne", "UserOne@mail.tst");
    private final User userTwo = new User(2L, "UserTwo", "UserTwo@mail.tst");

    @BeforeEach
    void beforeEach() {
        userRepository = mock(UserRepository.class);
        Mockito.when(userRepository.saveAndFlush(any())).then(invocation -> invocation.getArgument(0));
        Mockito.when(userRepository.save(any())).then(invocation -> invocation.getArgument(0));
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(userOne));
        Mockito.when(userRepository.findById(99L)).thenReturn(Optional.empty());
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);

        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void add() {
        //Пользователь без Email
        UserDto userDto = UserDto.builder()
                .id(0L)
                .name("UserOne")
                .build();

        //Отсутствие почты должно вызывать исключение
        //assertThrows(ValidationException.class, () -> userService.add(userDto));

        //После добавления почты создание пользователя должно происходить без ошибок
        userDto.setEmail("UserOne@mail.tst");
        assertEquals(userDto, userService.add(userDto));
    }

    @Test
    void getDto() {
        //Несуществующий пользователь должен вызывать исключение
        assertThrows(EntityNotFoundException.class, () -> userService.getDto(99L));

        //Получение корректного пользователя
        assertEquals(userDtoOne, userService.getDto(1L));
    }

    @Test
    void patch() {
        //Несуществующий пользователь должен вызывать исключение
        assertThrows(EntityNotFoundException.class, () -> userService.patch(userDtoErrUpd));

        //Обновление корректного пользователя
        assertEquals(userDtoOne, userService.patch(userDtoOne));
    }

    @Test
    void del() {
        //Несуществующий пользователь должен вызывать исключение
        assertThrows(EntityNotFoundException.class, () -> userService.del(99L));

        //Удаление существующего пользователя
        userService.del(1L);
        Mockito.verify(userRepository, Mockito.times(1))
                .deleteById(anyLong());
    }

    @Test
    void getAll() {
        Mockito.when(userRepository.findAll()).thenReturn(List.of(userOne, userTwo));

        //Получение всех пользователей
        assertEquals(List.of(userDtoOne, userDtoTwo), userService.getAll());
    }
}

package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

/**
 * Класс-контроллер для получения запросов по пользователям
 */
@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Создание пользователя
     */
    @PostMapping
    public UserDto postUser(@RequestBody UserDto user) {
        log.info("Creating user {}", user);
        return userService.add(user);
    }

    /**
     * Чтение пользователя
     */
    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        log.info("Get userId={}", userId);
        return userService.getDto(userId);
    }

    /**
     * Чтение пользователя
     */
    @GetMapping
    public Collection<UserDto> getAllUsers() {
        log.info("getAllUsers");
        return userService.getAll();
    }

    /**
     * Обновление пользователя
     */
    @PatchMapping("/{userId}")
    public UserDto patchUser(@RequestBody UserDto user, @PathVariable long userId) {
        log.info("Patch user {}, userId={}", user, userId);
        user.setId(userId);
        return userService.patch(user);
    }

    /**
     * Улаление пользователя
     */
    @DeleteMapping("/{userId}")
    public void delUser(@PathVariable long userId) {
        log.info("Delete userId={}", userId);
        userService.del(userId);
    }
}

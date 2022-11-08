package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

/**
 * Класс-контроллер для получения запросов по пользователям
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient userClient;

    /**
     * Создание пользователя
     */
    @PostMapping
    public ResponseEntity<Object> postUser(@Valid @RequestBody UserDto user) {
        log.info("Creating user {}", user);
        return userClient.add(user);
    }

    /**
     * Чтение пользователя
     */
    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable long userId) {
        log.info("Get userId={}", userId);
        return userClient.getDto(userId);
    }

    /**
     * Чтение пользователя
     */
    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("getAllUsers");
        return userClient.getAll();
    }


    /**
     * Обновление пользователя
     */
    @PatchMapping("/{userId}")
    public ResponseEntity<Object> patchUser(@RequestBody UserDto user, @PathVariable long userId) {
        log.info("Patch user {}, userId={}", user, userId);
        return userClient.patch(user, userId);
    }

    /**
     * Улаление пользователя
     */
    @DeleteMapping("/{userId}")
    public void delUser(@PathVariable long userId) {
        log.info("Delete userId={}", userId);
        userClient.del(userId);
    }
}

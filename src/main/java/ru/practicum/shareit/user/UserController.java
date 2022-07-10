package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

/**
 * Класс-контроллер для получения запросов по пользователям
 */
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
    public UserDto postUser(@Valid @RequestBody UserDto user){
        return userService.add(user);
    }

    /**
     * Чтение пользователя
     */
    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId){
        return UserMapper.toUserDto(userService.get(userId));
    }

    /**
     * Чтение пользователя
     */
    @GetMapping
    public Collection<UserDto> getAllUsers(){
        return userService.getAll();
    }


    /**
     * Обновление пользователя
     */
    @PatchMapping("/{userId}")
    public UserDto patchUser(@Valid @RequestBody UserDto user,
                           @PathVariable long userId){
        user.setId(userId);
        return userService.patch(user);
    }

    /**
     * Улаление пользователя
     */
    @DeleteMapping("/{userId}")
    public void delUser(@PathVariable long userId){
        userService.del(userId);
    }
}

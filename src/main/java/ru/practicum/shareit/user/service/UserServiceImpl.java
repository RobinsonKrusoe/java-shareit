package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.errorHandle.exception.EntityAlreadyExistException;
import ru.practicum.shareit.errorHandle.exception.EntityNotFoundException;
import ru.practicum.shareit.errorHandle.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.*;

@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    Map<Long, User> users = new HashMap<>();
    long iter = 1;
    @Override
    public UserDto add(UserDto user) {
        user.setId(iter);
        User userToIns = UserMapper.toUser(user);
        checkEmail(userToIns);
        users.put(iter++, userToIns);
        return user;
    }

    @Override
    public User get(long id) {
        if(users.containsKey(id)){
            return users.get(id);
        }else {
            throw new EntityNotFoundException("Пользователь не существует!");
        }
    }

    @Override
    public UserDto patch(UserDto user) {
        User userInBase = null;
        if(users.containsKey(user.getId())){
            userInBase = users.get(user.getId());
            if(user.getName() != null){
                userInBase.setName(user.getName());
            }
            if(user.getEmail() != null){
                checkEmail(UserMapper.toUser(user));
                userInBase.setEmail(user.getEmail());
            }
        }else {
            throw new EntityNotFoundException("Попытка обновления несуществующего пользователя!");
        }
        return UserMapper.toUserDto(userInBase);
    }

    @Override
    public void del(long id) {
        if(users.containsKey(id)){
            users.remove(id);
        }else {
            throw new EntityNotFoundException("Попытка удаления несуществующего пользователя!");
        }
    }

    public Collection<UserDto> getAll(){
        List<UserDto> ret = new ArrayList<>();
        for (User u : users.values()) {
            ret.add(UserMapper.toUserDto(u));
        }
        return ret;
    }

    private void checkEmail(User user){
        if(user.getEmail() == null){
            throw  new ValidationException("Email не может быть пустым!");
        }
        for (User u : users.values()) {
            if(u.getId() != user.getId() && user.getEmail().equals(u.getEmail())){
                throw new EntityAlreadyExistException("Пользователь с Email " + user.getEmail() + " уже существует!");
            }
        }
    }
}

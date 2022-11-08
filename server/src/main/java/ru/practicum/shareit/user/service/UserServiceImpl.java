package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import ru.practicum.shareit.errorHandle.exception.EntityNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.*;

@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public UserDto add(UserDto user) {
        User userToIns = UserMapper.toUser(user);

        userToIns = repository.saveAndFlush(userToIns);

        return UserMapper.toUserDto(userToIns);
    }

    @Override
    public UserDto getDto(long id) {
        return UserMapper.toUserDto(getUser(id));
    }

    @Override
    public User getUser(long id) {
        Optional<User> user = repository.findById(id);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new EntityNotFoundException("Пользователь не существует!");
        }
    }

    @Override
    public UserDto patch(UserDto user) {
        Optional<User> userInBase = null;
        if (user != null && user.getId() != null) {
            userInBase = repository.findById(user.getId());
        }

        if (userInBase.isPresent()) {
            if (user.getName() != null) {
                userInBase.get().setName(user.getName());
            }
            if (user.getEmail() != null) {
                userInBase.get().setEmail(user.getEmail());
            }
            repository.save(userInBase.get());
        } else {
            throw new EntityNotFoundException("Попытка обновления несуществующего пользователя!");
        }
        return UserMapper.toUserDto(userInBase.get());
    }

    @Override
    public void del(long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Попытка удаления несуществующего пользователя!");
        }
    }

    public Collection<UserDto> getAll() {
        List<UserDto> ret = new ArrayList<>();
        for (User u : repository.findAll()) {
            ret.add(UserMapper.toUserDto(u));
        }
        return ret;
    }
}

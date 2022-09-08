package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

public class UserMapper {
    public static UserDto toUserDto(User user){
        if(user != null) {
            return UserDto.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .build();
        } else {
            return null;
        }
    }
    public static User toUser(UserDto userDto){
        if(userDto != null) {
            User user = new User();
            user.setName(userDto.getName());
            user.setEmail(userDto.getEmail());
            return user;
        } else {
            return null;
        }
    }
}

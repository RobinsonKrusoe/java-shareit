package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Класс для отображении данных о пользователе
 */
@Data
@Builder
public class UserDto {
    private Long id;        //уникальный идентификатор пользователя
    private String name;    //имя или логин пользователя
    private String email;   //адрес электронной почты
                            //(два пользователя не могут иметь одинаковый адрес электронной почты).
}

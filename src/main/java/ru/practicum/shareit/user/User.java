package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;

/**
 * Класс пользователя
 */
@Data
@Builder
public class User {
    @NotNull
    private long id;        //уникальный идентификатор пользователя

    @NotNull
    private String name;    //имя или логин пользователя

    @NotNull
    @Email
    private String email;   //адрес электронной почты
                            //(два пользователя не могут иметь одинаковый адрес электронной почты).
}

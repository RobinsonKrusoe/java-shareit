package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * Класс для отображении данных о пользователе
 */
@Data
@Builder
public class UserDto {
    private Long id;        //уникальный идентификатор пользователя
    private String name;    //имя или логин пользователя
    @Email
    @NotBlank
    private String email;   //адрес электронной почты
                            //(два пользователя не могут иметь одинаковый адрес электронной почты).
}

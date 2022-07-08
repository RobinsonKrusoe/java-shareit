package ru.practicum.shareit.requests;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

import java.util.Date;

/**
 * Класс запроса для бронирования
 */
@Data
@Builder
public class ItemRequest {
    private long id;            //уникальный идентификатор запроса;
    private String description; //текст запроса, содержащий описание требуемой вещи;
    private long requestor;     //пользователь, создавший запрос;
    private Date created;       //дата и время создания запроса.
}

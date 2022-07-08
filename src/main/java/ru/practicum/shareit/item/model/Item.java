package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;

/**
 * Класс вещи для аренды
 */
@Data
@Builder
public class Item {
    @NotNull
    private long id;            //уникальный идентификатор вещи;
    private String name;        //краткое название;
    private String description; //развёрнутое описание;
    private boolean available;  //статус о том, доступна или нет вещь для аренды;
    private Long owner;         //владелец вещи;
    private Long request;       //если вещь была создана по запросу другого пользователя, то в этом поле будет храниться
                                //ссылка на соответствующий запрос.
}

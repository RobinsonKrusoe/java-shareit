package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

/**
 * Класс представления для элемента
 */
@Data
@Builder
public class ItemDto {
    private Long id;                //уникальный идентификатор вещи;
    private String name;            //краткое название;
    private String description;     //развёрнутое описание;
    private Boolean available;      //статус о том, доступна или нет вещь для аренды;
    private UserDto owner;          //владелец вещи;
    private ItemRequestDto request; //если вещь была создана по запросу другого пользователя, то в этом поле будет храниться
                                    //ссылка на соответствующий запрос.
    private BookingIner lastBooking;
    private BookingIner nextBooking;

    private Collection<CommentDto> comments;

    @Data
    @AllArgsConstructor
    public static class BookingIner {
        private Long id;
        private Long bookerId;
    }
}

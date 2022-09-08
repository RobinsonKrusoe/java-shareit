package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Date;

/**
 * Класс представления для бронирования вещи
 */
@Data
@Builder
public class BookingDto {
    private Long id;        //уникальный идентификатор бронирования;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date start;     //дата начала бронирования;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date end;       //дата конца бронирования;
    private ItemDto item;   //вещь, которую пользователь бронирует;
    private UserDto booker;    //пользователь, который осуществляет бронирование;
    private String status;  //статус бронирования.
                            //Может принимать одно из следующих значений: WAITING, APPROVED, REJECTED, CANCELED
}

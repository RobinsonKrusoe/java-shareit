package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * Класс представления для бронирования вещи (входящий вариант)
 */
@Data
@Builder
public class BookingInDto {
    private Long itemId;    //уникальный идентификатор бронирования;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date start;     //дата начала бронирования;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date end;       //дата конца бронирования;
}

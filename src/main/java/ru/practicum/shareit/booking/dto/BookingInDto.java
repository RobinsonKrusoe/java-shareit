package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Класс представления для бронирования вещи (входящий вариант)
 */
@Data
@Builder
public class BookingInDto {
    private Long itemId;    //уникальный идентификатор бронирования;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime  start;     //дата начала бронирования;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime end;       //дата конца бронирования;
}

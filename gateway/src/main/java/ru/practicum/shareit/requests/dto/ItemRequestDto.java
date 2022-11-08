package ru.practicum.shareit.requests.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import javax.validation.constraints.NotBlank;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Класс отображения для запроса вещи
 */
@Data
@Builder
public class ItemRequestDto {
    private Long id;                //уникальный идентификатор запроса;
    @NotBlank
    @NonNull
    private String description;     //текст запроса, содержащий описание требуемой вещи;
    private UserDto requestor;      //пользователь, создавший запрос;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime created;   //дата и время создания запроса.
    private List<ItemDto> items;
}

package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Date;

@Data
@Builder
public class CommentDto {
    private Long id;            //уникальный идентификатор комментария
    private String text;        //содержимое комментария
    private ItemDto item;       //вещь, к которой относится комментарий
    private UserDto author;     //автор комментария
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date created;       //дата создания комментария
}

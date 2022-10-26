package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    private Long id;            //уникальный идентификатор комментария
    private String text;        //содержимое комментария
    private ItemDto item;       //вещь, к которой относится комментарий
//    private UserDto author;     //автор комментария
    private String authorName;     //автор комментария
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime created;       //дата создания комментария
}

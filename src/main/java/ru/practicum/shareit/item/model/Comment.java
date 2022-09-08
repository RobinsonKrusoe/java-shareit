package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;            //уникальный идентификатор комментария

    private String text;        //содержимое комментария

    @ManyToOne
    @JoinColumn(name="item_id")
    private Item item;          //вещь, к которой относится комментарий

    @ManyToOne
    @JoinColumn(name="author_id")
    private User author;        //автор комментария

    private Date created;       //дата создания комментария
}

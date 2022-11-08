package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.persistence.*;

/**
 * Класс вещи для аренды
 */
@Entity
@Table(name = "items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;            //уникальный идентификатор вещи;

    private String name;        //краткое название;

    private String description; //развёрнутое описание;

    @Column(name = "is_available")
    private boolean available;  //статус о том, доступна или нет вещь для аренды;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;         //владелец вещи;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request;//если вещь была создана по запросу другого пользователя, то в этом поле будет храниться
                                //ссылка на соответствующий запрос.
}

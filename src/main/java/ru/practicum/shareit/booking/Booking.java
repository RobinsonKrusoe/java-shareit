package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Класс бронирования
 */
@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;                //уникальный идентификатор бронирования;

    @Column(name = "start_date")
    private LocalDateTime start;             //дата начала бронирования;

    @Column(name = "end_date")
    private LocalDateTime  end;               //дата конца бронирования;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;              //вещь, которую пользователь бронирует;

    @ManyToOne
    @JoinColumn(name = "booker_id")
    private User booker;            //пользователь, который осуществляет бронирование;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;   //статус бронирования.
                                    //Может принимать одно из следующих значений: WAITING, APPROVED, REJECTED, CANCELED
}

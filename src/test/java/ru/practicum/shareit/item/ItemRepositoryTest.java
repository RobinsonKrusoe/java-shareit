package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRepositoryTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRepository repository;

    @Test
    void search() {
        User userOne = new User(0L, "UserOne", "UserOne@mail.tst");

        Item itemOne = new Item(0L,
                "Аккумуляторная дрель",
                "Аккумуляторная дрель + аккумулятор",
                true,
                userOne,
                null);

        Item itemTwo = new Item(0L,
                "Отвертка",
                "Аккумуляторная отвертка",
                true,
                userOne,
                null);

        Item itemThree = new Item(0L,
                "Аккумулятор",
                "Аккумулятор переносной",
                false,
                userOne,
                null);

        em.persist(userOne);
        em.persist(itemOne);
        em.persist(itemTwo);
        em.persist(itemThree);

        List<Item> bdItems = repository.search("аккум", PageRequest.of(0, 3)).getContent();

        assertEquals(List.of(itemOne, itemTwo), bdItems);
    }

    @Test
    void findLastBooking() {
        User userOne = new User(0L, "UserOne", "UserOne@mail.tst");
        User userTwo = new User(0L, "UserTwo", "UserTwo@mail.tst");

        Item itemOne = new Item(0L,
                "Аккумуляторная дрель",
                "Аккумуляторная дрель + аккумулятор",
                true,
                userOne,
                null);

        Booking bookingOne = new Booking(0L,
                LocalDateTime.now().minusHours(24),
                LocalDateTime.now().minusHours(12),
                itemOne,
                userTwo,
                BookingStatus.APPROVED);

        Booking bookingTwo = new Booking(0L,
                LocalDateTime.now().minusHours(10),
                LocalDateTime.now().minusHours(5),
                itemOne,
                userTwo,
                BookingStatus.APPROVED);

        ItemDto.Booking lastBooking = new ItemDto.Booking(2L, 2L);

        em.persist(userOne);
        em.persist(userTwo);
        em.persist(itemOne);
        em.persist(bookingOne);
        em.persist(bookingTwo);

        Object[] o = repository.findLastBooking(1L, new Date()).get(0);
        assertEquals(lastBooking, new ItemDto.Booking(Long.valueOf(o[0].toString()), Long.valueOf(o[1].toString())));
    }

    @Test
    void findNextBooking() {
        User userOne = new User(0L, "UserOne", "UserOne@mail.tst");
        User userTwo = new User(0L, "UserTwo", "UserTwo@mail.tst");

        Item itemOne = new Item(0L,
                "Аккумуляторная дрель",
                "Аккумуляторная дрель + аккумулятор",
                true,
                userOne,
                null);

        Booking bookingOne = new Booking(0L,
                LocalDateTime.now().plusHours(12),
                LocalDateTime.now().plusHours(24),
                itemOne,
                userTwo,
                BookingStatus.APPROVED);

        Booking bookingTwo = new Booking(0L,
                LocalDateTime.now().plusHours(5),
                LocalDateTime.now().plusHours(10),
                itemOne,
                userTwo,
                BookingStatus.APPROVED);

        ItemDto.Booking nextBooking = new ItemDto.Booking(2L, 2L);

        em.persist(userOne);
        em.persist(userTwo);
        em.persist(itemOne);
        em.persist(bookingOne);
        em.persist(bookingTwo);

        Object[] o = repository.findNextBooking(1L, new Date()).get(0);
        assertEquals(nextBooking, new ItemDto.Booking(Long.valueOf(o[0].toString()), Long.valueOf(o[1].toString())));
    }
}

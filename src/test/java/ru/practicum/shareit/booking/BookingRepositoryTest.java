package ru.practicum.shareit.booking;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookingRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private BookingRepository repository;

    @Test
    void isBooked() {
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

        em.persist(userOne);
        em.persist(userTwo);
        em.persist(itemOne);
        em.persist(bookingOne);
        em.persist(bookingTwo);

        //Выбранная дата пересекается с двумя бронированиями
        assertEquals(2L,
                repository.isBooked(1L,
                        LocalDateTime.now().plusHours(9),
                        LocalDateTime.now().plusHours(13)));

        assertEquals(1L,
                repository.isBooked(1L,
                        LocalDateTime.now().plusHours(9),
                        LocalDateTime.now().plusHours(10)));
    }
}

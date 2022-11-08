package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplDBTest {
    private final EntityManager em;
    private final BookingServiceImpl service;

//    @AfterEach
//    void afterEach() {
//        em.createNativeQuery("truncate bookings restart identity cascade");
//        em.createNativeQuery("truncate comments restart identity cascade");
//        em.createNativeQuery("truncate item_requests restart identity cascade");
//        em.createNativeQuery("truncate items restart identity cascade");
//        em.createNativeQuery("truncate users restart identity cascade");
//    }

    @Test
    void testAddDB() {
        LocalDateTime startBooking = LocalDateTime.now().plusHours(2);
        LocalDateTime endBooking = LocalDateTime.now().plusHours(24);

        User userOne = new User(0L, "UserBSOne", "UserBSOne@mail.tst");
        User userTwo = new User(0L, "UserBSTwo", "UserBSTwo@mail.tst");

        Item item = new Item(0L,
                "Вещь для бронирования",
                "Вещь для проверки бронирования",
                true,
                userOne,
                null);

        em.persist(userOne);
        em.persist(userTwo);
        em.persist(item);

        BookingInDto bookingInDto = BookingInDto.builder()
                .itemId(item.getId())
                .start(startBooking)
                .end(endBooking)
                .build();

        BookingDto bookingDto = service.add(bookingInDto, userTwo.getId());

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);

        Booking booking = query
                .setParameter("id", bookingDto.getId())
                .getSingleResult();

        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getStart(), startBooking);
        assertEquals(booking.getEnd(), endBooking);
        assertEquals(booking.getStatus(), BookingStatus.WAITING);
    }
}

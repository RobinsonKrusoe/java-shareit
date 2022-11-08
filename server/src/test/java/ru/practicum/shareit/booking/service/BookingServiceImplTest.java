package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.errorHandle.exception.EntityNotFoundException;
import ru.practicum.shareit.errorHandle.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;

class BookingServiceImplTest {
    BookingRepository repository;
    BookingService bookingService;
    ItemService itemService;
    UserService userService;

    private final User userOne = new User(1L, "UserOne", "UserOne@mail.tst");
    private final User userTwo = new User(2L, "UserTwo", "UserTwo@mail.tst");

    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("UserOne")
            .email("UserOne@mail.tst")
            .build();

    private final UserDto userDtoTwo = UserDto.builder()
            .id(2L)
            .name("UserTwo")
            .email("UserTwo@mail.tst")
            .build();
    private final Item item = new Item(1L,
            "Аккумуляторная дрель",
            "Аккумуляторная дрель + аккумулятор",
            true,
            userOne,
            null);

    private final Item itemTwo = new Item(2L,
            "Недоступная вещь",
            "Вещь у которой выставлен флаг, что она недоступна",
            false,
            userOne,
            null);

    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Аккумуляторная дрель")
            .description("Аккумуляторная дрель + аккумулятор")
            .available(true)
            .owner(userDto)
            .build();

    private final LocalDateTime startBooking = LocalDateTime.now().plusHours(2);
    private final LocalDateTime endBooking = LocalDateTime.now().plusHours(24);

    private final Booking booking = new Booking(1L,
            startBooking,
            endBooking,
            item,
            userTwo,
            BookingStatus.WAITING);

    BookingDto bookingDto = BookingDto.builder()
            .id(1L)
            .item(itemDto)
            .start(startBooking)
            .end(endBooking)
            .booker(userDtoTwo)
            .status("WAITING")
            .build();

    @BeforeEach
    void beforeEach() {
        repository = mock(BookingRepository.class);
        userService = mock(UserService.class);
        itemService = mock(ItemService.class);

        Mockito.when(repository.saveAndFlush(any())).then(invocation -> invocation.getArgument(0));
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(booking));
        Mockito.when(repository.findById(99L)).thenReturn(Optional.empty());

        Mockito.when(userService.getUser(1L)).thenReturn(userOne);
        Mockito.when(userService.getUser(2L)).thenReturn(userTwo);
        Mockito.when(itemService.getItem(1L)).thenReturn(item);
        Mockito.when(itemService.getItem(2L)).thenReturn(itemTwo);

        bookingService = new BookingServiceImpl(repository, itemService, userService);
    }

    @Test
    void add() {
        LocalDateTime startBooking = LocalDateTime.now().plusHours(2);
        LocalDateTime endBooking = LocalDateTime.now().plusHours(24);

        BookingInDto bookingInDto = BookingInDto.builder()
                .itemId(1L)
                .build();

        BookingDto bookingDto = BookingDto.builder()
                .id(0L)
                .item(itemDto)
                .start(startBooking)
                .end(endBooking)
                .booker(userDtoTwo)
                .status("WAITING")
                .build();

        Booking booking1 = BookingMapper.toBooking(bookingDto);

        //Попытка забронировать свою вещь должна вызывать исключение
        assertThrows(EntityNotFoundException.class, () -> bookingService.add(bookingInDto, 1L));

        //Попытка забронировать вещь на пустую или некорректную дату должна вызывать исключение
        //assertThrows(ValidationException.class, () -> bookingService.add(bookingInDto, 2L));

        bookingInDto.setItemId(2L);
        //Попытка забронировать вещь, которая помечена как недоступная, должна вызывать исключение
        assertThrows(ValidationException.class, () -> bookingService.add(bookingInDto, 2L));

        bookingInDto.setItemId(1L);
        bookingInDto.setStart(startBooking);
        bookingInDto.setEnd(endBooking);
        assertEquals(bookingDto, bookingService.add(bookingInDto, 2L));
    }

    @Test
    void updateStatus() {
        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .item(itemDto)
                .start(startBooking)
                .end(endBooking)
                .booker(userDtoTwo)
                .status("APPROVED")
                .build();

        //Несуществующее бронирокание должно вызывать исключение
        assertThrows(EntityNotFoundException.class,
                () -> bookingService.updateStatus(99L, 1L, true));

        //Попытка обновить не своё бронирование должно вызывать исключение
        assertThrows(EntityNotFoundException.class,
                () -> bookingService.updateStatus(1L, 2L, true));


        assertEquals(bookingDto, bookingService.updateStatus(1L, 1L, true));

        //Некорректный новый статус (повторный перевод) должен вызывать исключение
        assertThrows(ValidationException.class,
                () -> bookingService.updateStatus(1L, 1L, true));

        bookingDto.setStatus("REJECTED");
        assertEquals(bookingDto, bookingService.updateStatus(1L, 1L, false));
    }

    @Test
    void getBooking() {
        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .item(itemDto)
                .start(startBooking)
                .end(endBooking)
                .booker(userDtoTwo)
                .status("WAITING")
                .build();

        //Несуществующее бронирокание должно вызывать исключение
        assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBooking(99L, 1L));

        assertEquals(bookingDto, bookingService.getBooking(1L, 1L));
    }

    @Test
    void findUserBookings() {
        Mockito.when(repository.findAllByBooker_IdOrderByStartDesc(any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        Mockito.when(repository.findAllByBooker_IdAndStartBeforeAndStatusOrderByStartDesc(any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        Mockito.when(repository.findAllByBooker_IdAndStartAfterOrderByStartDesc(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        Mockito.when(repository.findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        Mockito.when(repository.findAllByBooker_IdAndStatusOrderByStartDesc(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        //Некорректные параметры порции данных должны вызывать исключение
        assertThrows(IllegalArgumentException.class,
                () -> bookingService.findUserBookings("ALL", 1L, -1, 1));

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.findUserBookings("ALL", 1L, 0, -1));


        //Некорректный статус должен вызывать исключение
        assertThrows(IllegalArgumentException.class,
                () -> bookingService.findUserBookings("BAD_STATUS", 1L, 0, 1));

        assertEquals(List.of(bookingDto), bookingService.findUserBookings("ALL", 1L, 0, 1));
        assertEquals(List.of(bookingDto), bookingService.findUserBookings("PAST", 1L, 0, 1));
        assertEquals(List.of(bookingDto), bookingService.findUserBookings("FUTURE", 1L, 0, 1));
        assertEquals(List.of(bookingDto), bookingService.findUserBookings("CURRENT", 1L, 0, 1));
        assertEquals(List.of(bookingDto), bookingService.findUserBookings("WAITING", 1L, 0, 1));
    }

    @Test
    void findOwnerBookings() {
        Mockito.when(repository.findAllByItem_Owner_IdOrderByStartDesc(any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        Mockito.when(repository.findAllByItem_Owner_IdAndStartBeforeAndStatusOrderByStartDesc(any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        Mockito.when(repository.findAllByItem_Owner_IdAndStartAfterOrderByStartDesc(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        Mockito.when(repository.findAllByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        Mockito.when(repository.findAllByItem_Owner_IdAndStatusOrderByStartDesc(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        //Некорректные параметры порции данных должны вызывать исключение
        assertThrows(IllegalArgumentException.class,
                () -> bookingService.findOwnerBookings("ALL", 1L, -1, 1));

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.findOwnerBookings("ALL", 1L, 0, -1));

        //Некорректный статус должен вызывать исключение
        assertThrows(IllegalArgumentException.class,
                () -> bookingService.findOwnerBookings("BAD_STATUS", 1L, 0, 1));

        assertEquals(List.of(bookingDto), bookingService.findOwnerBookings("ALL", 1L, 0, 1));
        assertEquals(List.of(bookingDto), bookingService.findOwnerBookings("PAST", 1L, 0, 1));
        assertEquals(List.of(bookingDto), bookingService.findOwnerBookings("FUTURE", 1L, 0, 1));
        assertEquals(List.of(bookingDto), bookingService.findOwnerBookings("CURRENT", 1L, 0, 1));
        assertEquals(List.of(bookingDto), bookingService.findOwnerBookings("WAITING", 1L, 0, 1));
    }

    @Test
    void checkBooker() {
        Mockito.when(repository.existsByItem_IdAndBooker_IdAndStatusAndEndBefore(any(), any(), any(), any()))
                .thenReturn(true);
        assertEquals(true, bookingService.checkBooker(1L, 1L));
    }
}

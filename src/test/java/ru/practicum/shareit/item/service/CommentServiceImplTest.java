package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.errorHandle.exception.ValidationException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.*;

import org.mockito.Mockito;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;

class CommentServiceImplTest {

    CommentRepository repository;
    BookingService bookingService;
    ItemService itemService;
    UserService userService;
    CommentService commentService;

    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("UserOne")
            .email("UserOne@mail.tst")
            .build();

    private final User userOne = new User(1L, "UserOne", "UserOne@mail.tst");
    private final User userTwo = new User(2L, "UserTwo", "UserTwo@mail.tst");
    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Аккумуляторная дрель")
            .description("Аккумуляторная дрель + аккумулятор")
            .available(true)
            .owner(userDto)
            //.lastBooking(new ItemDto.Booking(1L, 2L))
            //.nextBooking(new ItemDto.Booking(1L, 2L))
            .build();

    private final Item item = new Item(1L,
            "Аккумуляторная дрель",
            "Аккумуляторная дрель + аккумулятор",
            true,
            userOne,
            null);

    private final CommentDto commentDto = CommentDto.builder()
            .id(1L)
            .text("Отлично")
            .item(itemDto)
            .authorName("UserOne")
            .build();

    private final Comment comm = new Comment(1L, "Отлично", item, userOne, LocalDateTime.now());

    @BeforeEach
    void beforeEach() {
        repository = mock(CommentRepository.class);
        userService = mock(UserService.class);
        itemService = mock(ItemService.class);
        bookingService = mock(BookingService.class);

        Mockito.when(repository.saveAndFlush(any())).then(invocation -> invocation.getArgument(0));
        Mockito.when(repository.findAllByItem_Id(any())).thenReturn(List.of(comm));
        Mockito.when(userService.getUser(1L)).thenReturn(userOne);
        Mockito.when(userService.getUser(2L)).thenReturn(userTwo);
        Mockito.when(itemService.getItem(1L)).thenReturn(item);
        Mockito.when(bookingService.checkBooker(1L, 1L)).thenReturn(true);
        Mockito.when(bookingService.checkBooker(1L, 2L)).thenReturn(false);

        commentService = new CommentServiceImpl(repository, bookingService, itemService, userService);
    }

    @Test
    void add() {
        CommentDto comment = CommentDto.builder()
                .id(1L)
                .item(itemDto)
                .item(itemDto)
                .authorName("UserOne")
                .build();

        //Пустой комментарий должен вызывать исключение
        assertThrows(ValidationException.class, () -> commentService.add(comment, 1L, 1L));

        comment.setText("Отлично");
        //Если автор комментария не арендовал вещь это должно вызвыать исключение
        assertThrows(ValidationException.class, () -> commentService.add(comment, 1L, 2L));

        CommentDto commentOut = commentService.add(comment, 1L, 1L);
        commentOut.setCreated(null);    //Убираем дату из сравнения
        assertEquals(comment, commentOut);
    }

    @Test
    void findItemComments() {
        List<CommentDto> comments = new ArrayList<>(commentService.findItemComments(1L));
        comments.get(0).setCreated(null);
        assertEquals(List.of(commentDto), comments);
    }
}

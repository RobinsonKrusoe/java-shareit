package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.errorHandle.exception.ValidationException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Component
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final BookingService bookingService;
    private final ItemService itemService;
    private final UserService userService;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository,
                              @Lazy BookingService bookingService,
                              @Lazy ItemService itemService,
                              UserService userService) {
        this.commentRepository = commentRepository;
        this.bookingService = bookingService;
        this.itemService = itemService;
        this.userService = userService;
    }

    /**
     * Добавление отзыва к вещи
     *
     * @param commentDto
     * @param itemId
     * @param userId
     * @return
     */
    @Override
    public CommentDto add(CommentDto commentDto, Long itemId, Long userId) {
        Comment comment = CommentMapper.toComment(commentDto);
        Item item = itemService.getItem(itemId);
        User booker = userService.getUser(userId);

        if(commentDto.getText() == null || commentDto.getText().isEmpty()){
            throw new ValidationException("Комментарий не может быть пустым!");
        }

        //Проверка что автор арендовал комментируемый товар
        if (!bookingService.checkBooker(itemId, userId)){
            throw new ValidationException("Оставлять комментарии может только арендатор по завершении аренды!");
        }

        comment.setCreated(new Date());
        comment.setItem(item);
        comment.setAuthor(booker);

        comment = commentRepository.saveAndFlush(comment);

        return CommentMapper.toCommentDto(comment);
    }

    @Override
    public Collection<CommentDto> findItemComments(Long itemId) {
        List<CommentDto> ret = new ArrayList<>();
        for(Comment comment : commentRepository.findAllByItem_Id(itemId)){
            ret.add(CommentMapper.toCommentDto(comment));
        }
        return ret;
    }
}

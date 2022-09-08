package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.UserMapper;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        if(comment != null) {
            return CommentDto.builder()
                    .id(comment.getId())
                    .text(comment.getText())
                    .author(UserMapper.toUserDto(comment.getAuthor()))
                    .item(ItemMapper.toItemDto(comment.getItem()))
                    .created(comment.getCreated())
                    .build();
        } else {
            return null;
        }
    }

    public static Comment toComment(CommentDto commentDto) {
        if(commentDto != null) {
            Comment comment = new Comment();
            if (commentDto.getId() != null) {
                comment.setId(commentDto.getId());
            }
            comment.setText(commentDto.getText());
            comment.setAuthor(UserMapper.toUser(commentDto.getAuthor()));
            comment.setItem(ItemMapper.toItem(commentDto.getItem()));
            comment.setCreated(commentDto.getCreated());

            return comment;
        } else {
            return null;
        }
    }
}

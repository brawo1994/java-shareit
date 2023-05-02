package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

public final class CommentMapper {

    private CommentMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Comment toModel(CommentDto commentDto) {
        return Comment.builder()
                .text(commentDto.getText())
                .build();
    }

    public static CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }
}

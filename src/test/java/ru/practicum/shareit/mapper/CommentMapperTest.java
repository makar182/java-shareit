package ru.practicum.shareit.mapper;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentMapperTest {
    public Comment comment;
    public User user;
    public Item item;

    @BeforeEach
    void setValues() {
        this.comment = new Comment();
        comment.setId(1L);
        comment.setText("text");
        comment.setCreated(LocalDateTime.now());
        comment.setAuthor(new User());

        this.item = new Item();
        item.setRequest(new ItemRequest());
        item.setId(1L);
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);

        this.user = new User();
        user.setId(1L);
        user.setEmail("mail@mail.ru");
        user.setName("name");
    }

    @Test
    public void commentMapperToCommentDtoTest() {
        CommentResponseDto commentResponseDto = ItemMapper.toCommentResponseDto(comment);
        assertEquals(commentResponseDto.getId(), comment.getId());
        assertEquals(commentResponseDto.getText(), comment.getText());
        assertEquals(commentResponseDto.getCreated(), comment.getCreated());
        assertEquals(commentResponseDto.getAuthorName(), comment.getAuthor().getName());
    }
}

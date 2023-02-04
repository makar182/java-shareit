package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CommentResponseDto {
    //@NotNull
    private Long id;
    //@NotBlank
    private String text;
    //@NotBlank
    private String authorName;
    //@NotNull
    private LocalDateTime created;
}

package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemGetResponseDto;
import ru.practicum.shareit.item.dto.ItemMainResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

    @Mock
    private ItemService itemService;
    @InjectMocks
    private ItemController itemController;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private Item item;
    private ItemRequestDto itemRequestDto;
    private ItemMainResponseDto itemMainResponseDto;
    private ItemGetResponseDto itemGetResponseDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        Item item = new Item();
        item.setRequest(new ItemRequest());
        item.setId(1L);
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);

        itemRequestDto = ItemRequestDto.builder()
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();

        itemMainResponseDto = ItemMainResponseDto.builder()
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();

        itemGetResponseDto = ItemGetResponseDto.builder()
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    @Test
    public void createItemTest() throws Exception {
        when(itemService.addItem(any(), any())).thenReturn(item);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemMainResponseDto.getName())))
                .andExpect(jsonPath("$.description", is(itemMainResponseDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemMainResponseDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemMainResponseDto.getRequestId())));
    }

    @Test
    public void createCommentTest() throws Exception {
        Comment comment = new Comment();
        comment.setCreated(LocalDateTime.now());
        comment.setId(1L);
        comment.setText("text");

        CommentResponseDto commentResponseDto = CommentResponseDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .created(comment.getCreated())
                .build();

        when(itemService.addComment(any(), any(), any())).thenReturn(comment);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(comment))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(comment.getId()), Integer.class))
                .andExpect(jsonPath("$.created", is(notNullValue())))
                .andExpect(jsonPath("$.text", is(comment.getText())));
    }

    @Test
    public void updateItemTest() throws Exception {
        when(itemService.updateItem(any(), any(), any())).thenReturn(item);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemMainResponseDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemMainResponseDto.getName())))
                .andExpect(jsonPath("$.description", is(itemMainResponseDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemMainResponseDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemMainResponseDto.getRequestId())));
    }

    @Test
    public void getItemByIdTest() throws Exception {
        when(itemService.getItemById(any(), any())).thenReturn(itemGetResponseDto);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemGetResponseDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemGetResponseDto.getName())))
                .andExpect(jsonPath("$.description", is(itemGetResponseDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemGetResponseDto.getAvailable())));
    }

    @Test
    public void getAllItemsOwnerTest() throws Exception {
        when(itemService.getItemsByUserId(any(), any(), any())).thenReturn(List.of(itemGetResponseDto));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(itemGetResponseDto.getId())))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder(itemGetResponseDto.getName())))
                .andExpect(jsonPath("$[*].description", containsInAnyOrder(itemGetResponseDto.getDescription())))
                .andExpect(jsonPath("$[*].available", containsInAnyOrder(itemGetResponseDto.getAvailable())));
    }

//    @Test
//    public void getAllItemsOwnerWithPagination() throws Exception {
//        when(itemService.getAllItemsWithPagination(any(), any(), any())).thenReturn(List.of(itemDto));
//
//        mvc.perform(get("/items?from=0&size=20")
//                        .header("X-Sharer-User-Id", 1)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[*].id", containsInAnyOrder(itemDto.getId())))
//                .andExpect(jsonPath("$[*].name", containsInAnyOrder(itemDto.getName())))
//                .andExpect(jsonPath("$[*].description", containsInAnyOrder(itemDto.getDescription())))
//                .andExpect(jsonPath("$[*].available", containsInAnyOrder(itemDto.getAvailable())))
//                .andExpect(jsonPath("$[*].requestId", containsInAnyOrder(itemDto.getRequestId())));
//    }

    @Test
    public void searchItemByTextTest() throws Exception {
        when(itemService.getItemsByDescription(any(), any(), any(), any())).thenReturn(List.of(itemGetResponseDto));

        mvc.perform(get("/items/search?text=text")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(itemGetResponseDto.getId())))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder(itemGetResponseDto.getName())))
                .andExpect(jsonPath("$[*].description", containsInAnyOrder(itemGetResponseDto.getDescription())))
                .andExpect(jsonPath("$[*].available", containsInAnyOrder(itemGetResponseDto.getAvailable())));
    }

//    @Test
//    public void searchItemByTextTestWithPagination() throws Exception {
//        when(itemService.searchItemByTextWithPagination(any(), any(), any(), any())).thenReturn(List.of(item));
//
//        mvc.perform(get("/items/search?text=text&from=0&size=20")
//                        .header("X-Sharer-User-Id", 1)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[*].id", containsInAnyOrder(itemDto.getId())))
//                .andExpect(jsonPath("$[*].name", containsInAnyOrder(itemDto.getName())))
//                .andExpect(jsonPath("$[*].description", containsInAnyOrder(itemDto.getDescription())))
//                .andExpect(jsonPath("$[*].available", containsInAnyOrder(itemDto.getAvailable())))
//                .andExpect(jsonPath("$[*].requestId", containsInAnyOrder(itemDto.getRequestId())));
//    }
}

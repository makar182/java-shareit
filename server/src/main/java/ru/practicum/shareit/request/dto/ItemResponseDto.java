package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class ItemResponseDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<NestedItem> items;

    @Getter
    @Setter
    public static class NestedItem {
        private Long id;
        private String name;
        private String description;
        private Boolean available;
        private Long requestId;

        public NestedItem(Item item) {
            this.id = item.getId();
            this.name = item.getName();
            this.description = item.getDescription();
            this.available = item.getAvailable();
            this.requestId = item.getRequest().getId();
        }
    }
}

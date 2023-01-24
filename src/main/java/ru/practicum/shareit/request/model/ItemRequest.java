package ru.practicum.shareit.request.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "requests")
@NoArgsConstructor
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    private LocalDateTime created = LocalDateTime.now();
    @OneToOne
    @JoinColumn(name = "requester_id", referencedColumnName = "id")
    private User requester;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "request")
    private List<Item> items;

    public ItemRequest(String description, User requester, List<Item> items) {
        this.description = description;
        this.requester = requester;
        this.items = items;
    }
}

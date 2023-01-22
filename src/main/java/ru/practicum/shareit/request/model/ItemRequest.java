package ru.practicum.shareit.request.model;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    private LocalDateTime created = LocalDateTime.now();
    @OneToOne
    @JoinColumn(name = "requester_id", referencedColumnName = "id")
    private User requester;

    public ItemRequest(Long id, String description, User requester) {
        this.id = id;
        this.description = description;
        this.requester = requester;
    }
}

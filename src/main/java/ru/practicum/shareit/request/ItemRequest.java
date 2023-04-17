package ru.practicum.shareit.request;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-item-requests.
 */
@Entity
@Getter
@Setter
@Builder
@Table(name = "requests")
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(length = 1000, nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "requester_id", referencedColumnName = "id")
    private User requestor;
}
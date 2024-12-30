package ru.practicum.ewm.request.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.request.enums.RequestStatuses;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    Long requester;

    @Column(nullable = false)
    Long event;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    RequestStatuses status;

    @Column(nullable = false)
    LocalDateTime created;
}

package ru.practicum.ewm.event.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.enums.EventStates;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@EqualsAndHashCode(of = {"id"})
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(length = 2000)
    String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "category_id")
    Category category;

    @Column(name = "confirmed_requests")
    Long confirmedRequests;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "initiator_id")
    User initiator;

    @Column(name = "created_on")
    LocalDateTime createdOn;

    @Column(length = 7000)
    String description;

    @Column(name = "event_date")
    LocalDateTime eventDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "location_id")
    Location location;

    @Column
    boolean paid;

    @Column(name = "participant_limit")
    Long participantLimit;

    @Column(name = "request_moderation")
    boolean requestModeration;

    @Column(length = 120)
    String title;

    @Enumerated(EnumType.STRING)
    EventStates state;

    @Column(name = "published_on")
    LocalDateTime publishedOn;

    @Column
    Long views;
}

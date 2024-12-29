package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.event.enums.EventStates;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long id;
    String annotation;
    CategoryDto category;
    Long confirmedRequests;
    LocalDateTime createdOn;
    String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    UserShortDto initiator;
    LocationDto location;
    boolean paid;
    Long participantLimit;
    LocalDateTime publishedOn;
    boolean requestModeration;
    EventStates state;
    String title;
    Long views;
}

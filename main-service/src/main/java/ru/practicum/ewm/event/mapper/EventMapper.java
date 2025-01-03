package ru.practicum.ewm.event.mapper;

import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.enums.EventStates;
import ru.practicum.ewm.event.enums.UpdateEventAdminStates;
import ru.practicum.ewm.event.enums.UpdateEventUserStates;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.user.dto.UserShortDto;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class EventMapper {
    private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Event mapNewEventToEvent(NewEventDto newEventDto, Category category, User user, Location location) {
        return Event.builder()
                .id(null)
                .annotation(newEventDto.getAnnotation())
                .category(category)
                .initiator(user)
                .confirmedRequests(0L)
                .createdOn(LocalDateTime.now())
                .description(newEventDto.getDescription())
                .eventDate(LocalDateTime.parse(newEventDto.getEventDate(), format))
                .location(location)
                .paid(newEventDto.getPaid() == null ? false : newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit() == null ? 0L : newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration() == null ? true : newEventDto.getRequestModeration())
                .title(newEventDto.getTitle())
                .state(EventStates.PENDING)
                .views(0L)
                .build();
    }

    public static EventFullDto mapEventToEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(new CategoryDto(event.getCategory().getId(), event.getCategory().getName()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()))
                .location(new LocationDto(event.getLocation().getLat(), event.getLocation().getLon()))
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.isRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public static EventShortDto mapEventToEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(new CategoryDto(event.getCategory().getId(), event.getCategory().getName()))
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate())
                .initiator(new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()))
                .paid(event.isPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public static Set<EventShortDto> mapSetOfEventsToEventsDto(Set<Event> events) {
        return events.stream()
                .map(EventMapper::mapEventToEventShortDto)
                .collect(Collectors.toSet());
    }

    public static Event updateEventDataByUser(Event event, UpdateEventUserRequest request, Category category) {
        if (request.getAnnotation() != null) {
            event.setAnnotation(request.getAnnotation());
        }

        if (category != null) {
            event.setCategory(category);
        }

        if (request.getDescription() != null) {
            event.setDescription(request.getDescription());
        }

        if (request.getEventDate() != null) {
            event.setEventDate(request.getEventDate());
        }

        if (request.getLocation() != null) {
            event.setLocation(new Location(event.getLocation().getId(), request.getLocation().getLat(),
                    request.getLocation().getLon()));
        }

        if (request.getPaid() != null) {
            event.setPaid(request.getPaid());
        }

        if (request.getParticipantLimit() != null) {
            event.setParticipantLimit(request.getParticipantLimit());
        }

        if (request.getRequestModeration() != null) {
            event.setRequestModeration(request.getRequestModeration());
        }

        if (request.getStateAction() != null) {
            if (request.getStateAction().equals(UpdateEventUserStates.SEND_TO_REVIEW)) {
                event.setState(EventStates.PENDING);
            } else if (request.getStateAction().equals(UpdateEventUserStates.CANCEL_REVIEW)) {
                event.setState(EventStates.CANCELED);
            }
        }

        if (request.getTitle() != null) {
            event.setTitle(request.getTitle());
        }

        return event;
    }

    public static Event updateEventDataByAdmin(Event event, UpdateEventAdminRequest request, Category category) {
        if (request.getAnnotation() != null) {
            event.setAnnotation(request.getAnnotation());
        }

        if (category != null) {
            event.setCategory(category);
        }

        if (request.getDescription() != null) {
            event.setDescription(request.getDescription());
        }

        if (request.getEventDate() != null) {
            event.setEventDate(request.getEventDate());
        }

        if (request.getLocation() != null) {
            event.setLocation(new Location(event.getLocation().getId(), request.getLocation().getLat(),
                    request.getLocation().getLon()));
        }

        if (request.getPaid() != null) {
            event.setPaid(request.getPaid());
        }

        if (request.getParticipantLimit() != null) {
            event.setParticipantLimit(request.getParticipantLimit());
        }

        if (request.getRequestModeration() != null) {
            event.setRequestModeration(request.getRequestModeration());
        }

        if (request.getStateAction() != null) {
            if (request.getStateAction().equals(UpdateEventAdminStates.PUBLISH_EVENT)) {
                event.setState(EventStates.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (request.getStateAction().equals(UpdateEventAdminStates.REJECT_EVENT)) {
                event.setState(EventStates.CANCELED);
            }
        }

        if (request.getTitle() != null) {
            event.setTitle(request.getTitle());
        }

        return event;
    }

    public static List<EventShortDto> mapToListShortDto(List<Event> events, Map<Long, Long> hits) {
        return events.stream()
                .map(event -> {
                    EventShortDto eventShortDto = EventMapper.mapEventToEventShortDto(event);
                    eventShortDto.setViews(hits.get(event.getId()));
                    return eventShortDto;
                }).toList();
    }
}

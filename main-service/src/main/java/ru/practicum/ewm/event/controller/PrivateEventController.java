package ru.practicum.ewm.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.service.PrivateEventService;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PrivateEventController {
    PrivateEventService service;

    @PostMapping("/{userId}/events")
    @ResponseStatus(code = HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable("userId") Long userId,
                                    @Valid @RequestBody NewEventDto newEventDto) {
        return service.createEvent(userId, newEventDto);
    }

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getAllUserEvents(@PathVariable("userId") Long userId,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(defaultValue = "10") @Positive int size) {
        return service.getAllUserEvents(userId, from, size);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getUserEventById(@PathVariable("userId") Long userId,
                                         @PathVariable("eventId") Long eventId) {
        return service.getUserEventById(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateEventByUser(@PathVariable("userId") Long userId,
                                          @PathVariable("eventId") Long eventId,
                                          @Valid @RequestBody UpdateEventUserRequest request) {
        return service.updateEventByUser(userId, eventId, request);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getAllUserRequests(@PathVariable("userId") Long userId,
                                                            @PathVariable("eventId") Long eventId) {
        return service.getAllUserRequests(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestStatuses(@PathVariable("userId") Long userId,
                                                          @PathVariable("eventId") Long eventId,
                                                          @Valid @RequestBody EventRequestStatusUpdateRequest request) {
        return service.updateRequestStatuses(userId, eventId, request);
    }
}

package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminEventService {
    List<EventFullDto> getAllEventsByCriteria(List<Long> users, List<String> states, List<Long> categories,
                                              LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from,
                                              Integer size);

    EventFullDto updateEventData(Long eventId, UpdateEventAdminRequest request);
}

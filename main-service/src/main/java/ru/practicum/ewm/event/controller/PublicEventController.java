package ru.practicum.ewm.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.service.PublicEventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PublicEventController {
    PublicEventService service;

    @GetMapping
    public List<EventShortDto> getEventsByCriteria(@RequestParam(required = false) String text,
                                                   @RequestParam(required = false) List<Long> categories,
                                                   @RequestParam(required = false) Boolean paid,
                                                   @RequestParam(required = false)
                                                       @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                       LocalDateTime rangeStart,
                                                   @RequestParam(required = false)
                                                       @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                       LocalDateTime rangeEnd,
                                                   @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                   @RequestParam(defaultValue = "EVENT_DATE") String sort,
                                                   @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero
                                                       Integer from,
                                                   @RequestParam(value = "size", defaultValue = "10") @Positive
                                                       Integer size,
                                                   HttpServletRequest request) {
        return service.getEventsByCriteria(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from,
                size, request);
    }

    @GetMapping("/{id}")
    public EventFullDto getEventById(@PathVariable("id") Long eventId, HttpServletRequest request) {
        return service.getEventById(eventId, request);
    }
}

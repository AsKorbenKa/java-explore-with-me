package ru.practicum.ewm.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.service.AdminEventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminEventController {
    AdminEventService service;

    @GetMapping
    public List<EventFullDto> getAllEventsByCriteria(@RequestParam(required = false) List<Long> users,
                                                     @RequestParam(required = false) List<String> states,
                                                     @RequestParam(required = false) List<Long> categories,
                                                     @RequestParam(required = false) @DateTimeFormat(pattern =
                                                             "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                     @RequestParam(required = false) @DateTimeFormat(pattern =
                                                             "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                     @RequestParam(value = "from", defaultValue = "0")
                                                         @PositiveOrZero Integer from,
                                                     @RequestParam(value = "size", defaultValue = "10")
                                                         @Positive Integer size) {
        return service.getAllEventsByCriteria(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventData(@PathVariable("eventId") Long eventId,
                                        @Valid @RequestBody UpdateEventAdminRequest request) {
        return service.updateEventData(eventId, request);
    }

}

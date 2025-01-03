package ru.practicum.ewm.request.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.service.RequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PrivateRequestController {
    RequestService service;

    @PostMapping("/{userId}/requests")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable("userId") Long userId,
                                                 @RequestParam(name = "eventId") Long eventId) {
        return service.createRequest(userId, eventId);
    }

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getAllUserRequests(@PathVariable("userId") Long userId) {
        return service.getAllUserRequests(userId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable("userId") Long userId,
                                                 @PathVariable("requestId") Long requestId) {
        return service.cancelRequest(userId, requestId);
    }
}

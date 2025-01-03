package ru.practicum.ewm.request.mapper;

import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.model.Request;

public class RequestMapper {
    public static ParticipationRequestDto mapRequestToDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .requester(request.getRequester())
                .event(request.getEvent())
                .status(request.getStatus())
                .created(request.getCreated())
                .build();
    }
}

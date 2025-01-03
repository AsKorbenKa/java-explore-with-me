package ru.practicum.ewm.compilation.mapper;

import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;

import java.util.List;
import java.util.Set;

public class CompilationMapper {
    public static Compilation mapNewCompilationToCompilation(NewCompilationDto newCompilationDto, Set<Event> events) {
        return Compilation.builder()
                .id(null)
                .events(events)
                .pinned(newCompilationDto.isPinned())
                .title(newCompilationDto.getTitle())
                .build();
    }

    public static CompilationDto mapCompilationToCompilationDto(Compilation compilation,
                                                                Set<EventShortDto> shortEvents) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .events(shortEvents)
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }

    public static Compilation mapUpdateCompilationToCompilation(UpdateCompilationRequest updateCompilationRequest,
                                                                Set<Event> events, Long compId) {
        return Compilation.builder()
                .id(compId)
                .events(events)
                .pinned(updateCompilationRequest.getPinned())
                .title(updateCompilationRequest.getTitle())
                .build();
    }

    public static List<CompilationDto> mapListOfCompToCompDto(List<Compilation> compilations) {
        return compilations.stream()
                .map(compilation -> {
                    return new CompilationDto(
                            compilation.getId(),
                            EventMapper.mapSetOfEventsToEventsDto(compilation.getEvents()),
                            compilation.getPinned(),
                            compilation.getTitle()
                    );
                }).toList();
    }

    public static Compilation updateCompData(Compilation compilation, Set<Event> events,
                                             UpdateCompilationRequest request) {
        if (!events.isEmpty()) {
            compilation.setEvents(events);
        }

        if (request.getPinned() != null) {
            compilation.setPinned(request.getPinned());
        }

        if (request.getTitle() != null) {
            compilation.setTitle(request.getTitle());
        }

        return compilation;
    }
}

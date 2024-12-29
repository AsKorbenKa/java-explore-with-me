package ru.practicum.ewm.compilation.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminCompilationServiceImpl implements AdminCompilationService {
    CompilationRepository repository;
    EventRepository eventRepository;

    @Autowired
    public AdminCompilationServiceImpl(CompilationRepository repository,
                                       EventRepository eventRepository) {
        this.repository = repository;
        this.eventRepository = eventRepository;
    }

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        log.info("Добавляем новую подборку событий.");

        Compilation savedCompilation = repository.save(CompilationMapper.mapNewCompilationToCompilation(
                 newCompilationDto, findEvents(newCompilationDto.getEvents())));

        Set<EventShortDto> shortEvents = getShortEvents(savedCompilation.getEvents());

        return CompilationMapper.mapCompilationToCompilationDto(savedCompilation, shortEvents);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        log.info("Обновляем данные подборки событий.");

        // Проверяем существует ли подборка событий
        Compilation compilation = findCompilation(compId);
        Set<Event> events = findEvents(updateCompilationRequest.getEvents());

        Compilation updatedCompilation = repository.save(CompilationMapper.updateCompData(compilation, events,
                updateCompilationRequest));

        Set<EventShortDto> shortEvents = getShortEvents(updatedCompilation.getEvents());

        return CompilationMapper.mapCompilationToCompilationDto(updatedCompilation, shortEvents);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        log.info("Удаляем подборку событие по ее id.");

        // Проверяем существует ли подборка событий
        findCompilation(compId);

        repository.deleteById(compId);
    }

    private Set<Event> findEvents(Set<Long> events) {
        return events == null ? Set.of() : eventRepository.findAllByIdIn(events);
    }

    private Compilation findCompilation(Long compId) {
        return repository.findById(compId).orElseThrow(() -> new NotFoundException(String.format("Подборка событий " +
                "с id=%d не найдена.", compId)));
    }

    private Set<EventShortDto> getShortEvents(Set<Event> events) {
        return events.stream()
                .map(EventMapper::mapEventToEventShortDto)
                .collect(Collectors.toSet());
    }
}

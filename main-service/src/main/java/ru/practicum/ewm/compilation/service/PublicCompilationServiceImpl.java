package ru.practicum.ewm.compilation.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PublicCompilationServiceImpl implements PublicCompilationService {
    CompilationRepository repository;

    @Autowired
    public PublicCompilationServiceImpl(CompilationRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getAllCompilations(Boolean pinned, int from, int size) {
        log.info("Получаем все подборки событий по параметрам.");
        Pageable pageable = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "id"));
        List<Compilation> compilations;

        if (pinned == null) {
            compilations = repository.findAll(pageable).stream().toList();
        } else {
            compilations = repository.findAllByPinned(pinned, pageable);
        }

        return CompilationMapper.mapListOfCompToCompDto(compilations);
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilationById(Long compId) {
        log.info("Получаем подборку событий по ее id.");
        Compilation compilation = repository.findById(compId).orElseThrow(() ->
                new NotFoundException(String.format("Подборка событий по id=%d не найдена.", compId)));

        Set<EventShortDto> shortEvents = EventMapper.mapSetOfEventsToEventsDto(compilation.getEvents());

        return CompilationMapper.mapCompilationToCompilationDto(compilation, shortEvents);
    }
}

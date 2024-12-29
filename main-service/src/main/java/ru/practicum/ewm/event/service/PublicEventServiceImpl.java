package ru.practicum.ewm.event.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.enums.EventStates;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.request.enums.RequestStatuses;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.statistic.Statistic;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PublicEventServiceImpl implements PublicEventService {
    EventRepository repository;
    Statistic statistic;
    RequestRepository requestRepository;
    StatsClient statsClient;

    @Override
    @Transactional
    public List<EventShortDto> getEventsByCriteria(String text, List<Long> categories, Boolean paid,
                                                   LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                   Boolean onlyAvailable, String sort, Integer from, Integer size,
                                                   HttpServletRequest request) {
        log.info("Получаем список всех событий по переданным условиям.");
        Specification<Event> specification = Specification.where(null);
        Pageable pageable = null;

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("Неверно переданы условия. Дата и время rangeStart не могут идти после " +
                    "rangeEnd.");
        }

        if (text != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")), "%" +
                                    text.toLowerCase() + "%"),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" +
                                    text.toLowerCase() + "%")
                    ));
        }

        if (categories != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("category").get("id").in(categories));
        }

        if (paid != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("paid"), paid));
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDateTime = Objects.requireNonNullElseGet(rangeStart, () -> now);
        specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThan(root.get("eventDate"), startDateTime));
        if (rangeEnd != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThan(root.get("eventDate"), rangeEnd));
        }

        if (onlyAvailable != null && onlyAvailable) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("participantLimit"), 0));
        }
        specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("state"), EventStates.PUBLISHED));

        pageable = switch (sort) {
            case "EVENT_DATE" -> PageRequest.of(from, size, Sort.by("eventDate"));
            case "VIEWS" -> PageRequest.of(from, size, Sort.by("views").descending());
            default -> throw new ValidationException("Неизвестный параметр сортировки: " + sort);
        };

        List<Event> events = repository.findAll(specification, pageable).getContent();

        events = events.stream()
                .peek(event -> event.setConfirmedRequests((long) requestRepository.findAllByEventAndStatus(
                        event.getId(), RequestStatuses.CONFIRMED).size()))
                .toList();

        Map<Long, Long> hits = statistic.getStatistic(events, Boolean.FALSE);

        statistic.saveStatistic(request);

        return EventMapper.mapToListShortDto(events, hits);
    }

    @Override
    @Transactional
    public EventFullDto getEventById(Long eventId, HttpServletRequest request) {
        log.info("Получение подробной информации об опубликованном по его идентификатору.");
        Event event = getEvent(eventId);
        if (!event.getState().equals(EventStates.PUBLISHED)) {
            throw new NotFoundException(String.format("Событие с id=%d не найдено.", eventId));
        }
        statistic.saveStatistic(request);
        Map<Long, Long> hits = statistic.getStatistic(
                event.getPublishedOn(),
                LocalDateTime.now(),
                List.of(request.getRequestURI()),
                Boolean.TRUE
        );

        System.out.println(hits);
        event.setConfirmedRequests((long) requestRepository.findAllByEventAndStatus(eventId,
                RequestStatuses.CONFIRMED).size());
        event.setViews(hits.getOrDefault(eventId, 0L));

        return EventMapper.mapEventToEventFullDto(event);
    }

    private Event getEvent(Long eventId) {
        log.info("Получаем событие по его id.");
        return repository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Событие с id=%d не найдено.", eventId)));
    }
}

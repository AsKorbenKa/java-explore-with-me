package ru.practicum.ewm.event.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.enums.EventStates;
import ru.practicum.ewm.event.enums.UpdateEventAdminStates;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConditionsNotMetException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.request.enums.RequestStatuses;
import ru.practicum.ewm.request.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminEventServiceImpl implements AdminEventService {
    EventRepository repository;
    CategoryRepository categoryRepository;
    RequestRepository requestRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getAllEventsByCriteria(List<Long> users, List<String> states, List<Long> categories,
                                                     LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from,
                                                     Integer size) {
        log.info("Получаем список всех событий по переданным условиям.");

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("Неверно переданы условия. Дата и время rangeStart не могут идти после " +
                    "rangeEnd.");
        }

        Specification<Event> specification = Specification.where(null);
        if (users != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("initiator").get("id").in(users));
        }

        if (states != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("state").as(String.class).in(states));
        }

        if (categories != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("category").get("id").in(categories));
        }

        if (rangeStart != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }

        if (rangeEnd != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }

        List<Event> events = repository.findAll(specification, PageRequest.of(from, size)).getContent();

        return events.stream()
                .map(event -> {
                    event.setConfirmedRequests((long) requestRepository.findAllByEventAndStatus(event.getId(),
                            RequestStatuses.CONFIRMED).size());
                    return EventMapper.mapEventToEventFullDto(event);
                    })
                .toList();
    }

    @Override
    @Transactional
    public EventFullDto updateEventData(Long eventId, UpdateEventAdminRequest request) {
        log.info("Обновляем данные события по запросу админа.");
        Event event = getEvent(eventId);
        Category category = null;

        if (request.getCategory() != null) {
            category = getCategory(request.getCategory());
        }

        if (request.getEventDate() != null && request.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ValidationException("Дата начала изменяемого события должна быть не ранее чем за час от " +
                    "даты публикации.");
        }

        if (!event.getState().equals(EventStates.PENDING) && request.getStateAction()
                .equals(UpdateEventAdminStates.PUBLISH_EVENT)) {
            throw new ConditionsNotMetException("Событие можно публиковать, только если оно в состоянии ожидания " +
                    "публикации.");
        }

        if (event.getState().equals(EventStates.PUBLISHED) && request.getStateAction()
                .equals(UpdateEventAdminStates.REJECT_EVENT)) {
            throw new ConditionsNotMetException("Событие можно отклонить, только если оно еще не опубликовано.");
        }

        Event updatedEvent = repository.save(EventMapper.updateEventDataByAdmin(event, request, category));
        return EventMapper.mapEventToEventFullDto(updatedEvent);
    }

    private Event getEvent(Long eventId) {
        log.info("Получаем событие по его id.");
        return repository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Событие с id=%d не найдено.", eventId)));
    }

    private Category getCategory(long categoryId) {
        log.info("Получаем категорию по её id.");
        return categoryRepository.findById(categoryId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id=%d не найден.", categoryId)));
    }
}

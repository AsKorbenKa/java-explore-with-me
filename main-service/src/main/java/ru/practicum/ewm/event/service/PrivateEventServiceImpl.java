package ru.practicum.ewm.event.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.enums.EventStates;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.mapper.LocationMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.repository.LocationRepository;
import ru.practicum.ewm.exception.ConditionsNotMetException;
import ru.practicum.ewm.exception.DuplicatedDataException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.enums.RequestStatuses;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PrivateEventServiceImpl implements PrivateEventService {
    EventRepository repository;
    UserRepository userRepository;
    CategoryRepository categoryRepository;
    LocationRepository locationRepository;
    RequestRepository requestRepository;

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        log.info("Добавляем новое событие в базу данных.");
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime eventDate = LocalDateTime.parse(newEventDto.getEventDate(), format);

        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Ошибка при создании события. Поле: eventDate. Ошибка: начало события " +
                    "должно быть более чем через 2 часа от текущего времени. Значение: " + eventDate);
        }

        User user = getUser(userId);
        Category category = getCategory(newEventDto.getCategory());
        Location location = locationRepository.save(LocationMapper.mapLocationDtoToLocation(newEventDto.getLocation()));

        Event createdEvent = repository.save(EventMapper.mapNewEventToEvent(newEventDto, category, user, location));
        return EventMapper.mapEventToEventFullDto(createdEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllUserEvents(Long userId, int from, int size) {
        log.info("Получаем события, добавленные текущим пользователем.");
        Pageable pageable = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "id"));
        List<Event> result = repository.findAllByInitiatorId(userId, pageable);

        return result.stream().map(EventMapper::mapEventToEventShortDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getUserEventById(Long userId, Long eventId) {
        log.info("Получаем полную информацию о событии, добавленном текущим пользователем.");
        Event event = repository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() ->
                new NotFoundException(String.format("Событие с id=%d у пользователя с id=%d не найдено.", eventId,
                        userId)));

        return EventMapper.mapEventToEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest request) {
        log.info("Обновляем данные события по запросу инициатора события.");
        Event event = getEvent(eventId);
        Category category = null;

        if (request.getCategory() != null) {
            category = getCategory(request.getCategory());
        }

        if (!userId.equals(event.getInitiator().getId())) {
            throw new ConditionsNotMetException("Ошибка при обновлении данных события. Редактировать событие может" +
                    " только инициатор события.");
        }

        if (event.getState().equals(EventStates.PUBLISHED)) {
            throw new ConditionsNotMetException("Изменить данные можно только у неопубликованного или отмененного" +
                    " события.");
        }

        if (request.getEventDate() != null && request.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Дата и время, на которые намечено событие, не могут быть раньше, чем" +
                    " через два часа от текущего момента.");
        }

        Event updatedEvent = repository.save(EventMapper.updateEventDataByUser(event, request, category));

        return EventMapper.mapEventToEventFullDto(updatedEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getAllUserRequests(Long userId, Long eventId) {
        log.info("Получаем информацию о запросах на участи в событии текущего пользователя.");

        repository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() -> new NotFoundException(
                String.format("Пользователь с id=%d не создавал событие с id=%d.", userId, eventId)));

        List<Request> allRequests = requestRepository.findAllByEvent(eventId);

        return allRequests.stream()
                .map(RequestMapper::mapRequestToDto)
                .toList();
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestStatuses(Long userId, Long eventId,
                                                                EventRequestStatusUpdateRequest request) {
        log.info("Изменяем статус заявок на участие в событии текущего пользователя.");
        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
        List<Request> requests = requestRepository.findAllByIdIn(request.getRequestIds());
        Event event = getEvent(eventId);
        long alreadyConfirmedRequests = requestRepository.findAllByEventAndStatus(eventId,
                        RequestStatuses.CONFIRMED).size();

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConditionsNotMetException("Одобрять или отклонять заявки на участие в событии может только " +
                    "инициатор события.");
        }

        if (event.getParticipantLimit().equals(alreadyConfirmedRequests)) {
            throw new DuplicatedDataException(String.format("У события с id=%d достигнут лимит по заявкам.", eventId));
        }

        // Отклоняем все заявки если статус = REJECTED
        if (request.getStatus().equals(RequestStatuses.REJECTED.toString())) {
            log.info("Статус равен REJECTED, отклоняем все заявки.");
            for (Request req : requests) {
                if (!req.getStatus().equals(RequestStatuses.REJECTED)) {
                    req.setStatus(RequestStatuses.REJECTED);
                    requestRepository.save(req);
                }
            }
            rejectedRequests = requests.stream()
                    .map(RequestMapper::mapRequestToDto)
                    .toList();
            return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
        }

        // Нет лимита по заявкам, потому все заявки подтверждаем
        if (event.getParticipantLimit().equals(0L)) {
            log.info("Нет лимита по заявкам, подтверждаем все заявки.");
            for (Request req : requests) {
                if (!req.getStatus().equals(RequestStatuses.CONFIRMED)) {
                    req.setStatus(RequestStatuses.CONFIRMED);
                    requestRepository.save(req);
                }
            }
            confirmedRequests = requests.stream()
                    .map(RequestMapper::mapRequestToDto)
                    .toList();
        } else {
            // Есть лимит по заявкам и статус = CONFIRMED, потому проверяем сколько еще заявок можно одобрить
            // и проверяем статусы запросов
            if (alreadyConfirmedRequests + request.getRequestIds().size() <= event.getParticipantLimit()) {
                log.info("Лимит по заявкам позволяет одобрить все заявки. Изменяем статус всех заявок на CONFIRMED.");
                // Можем одобрить все заявки, одобряем и проверяем статус
                for (Request req : requests) {
                    if (!req.getStatus().equals(RequestStatuses.PENDING)) {
                        throw new ConditionsNotMetException(String.format("Одобрить можно только заявки с статусе " +
                                "PENDING. Статус заявки с id=%d %s", req.getId(), req.getStatus()));
                    }
                    req.setStatus(RequestStatuses.CONFIRMED);
                    requestRepository.save(req);
                }
                confirmedRequests = requests.stream()
                        .map(RequestMapper::mapRequestToDto)
                        .toList();

            } else {
                log.info("Лимит по заявкам не позволяет одобрить все заявки. Одобряем их часть.");
                // Можем одобрить только часть заявок. Вычисляем сколько можем одобрить и проверяем статусы.
                // Одобряем заявки пока не будет достигнут лимит
                for (Request req : requests.subList(0, (int) (event.getParticipantLimit() - alreadyConfirmedRequests))) {
                    if (!req.getStatus().equals(RequestStatuses.PENDING)) {
                        throw new ConditionsNotMetException(String.format("Одобрить можно только заявки с статусе " +
                                "PENDING. Статус заявки с id=%d %s", req.getId(), req.getStatus()));
                    }
                    req.setStatus(RequestStatuses.CONFIRMED);
                    requestRepository.save(req);
                    confirmedRequests.add(RequestMapper.mapRequestToDto(req));
                }
                log.info("Отклоняем оставшиеся заявки.");
                // Лимит был достигнут, отклоняем оставшиеся заявки
                for (Request req : requests.subList((int) (event.getParticipantLimit() - alreadyConfirmedRequests),
                        requests.size())) {
                    if (!req.getStatus().equals(RequestStatuses.REJECTED)) {
                        req.setStatus(RequestStatuses.REJECTED);
                        requestRepository.save(req);
                        rejectedRequests.add(RequestMapper.mapRequestToDto(req));
                    }
                }
            }
        }
        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

    private Event getEvent(Long eventId) {
        log.info("Получаем событие по его id.");
        return repository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Событие с id=%d не найдено.", eventId)));
    }

    private User getUser(Long userId) {
        log.info("Получаем пользователя по его id.");
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id=%d не найден.", userId)));
    }

    private Category getCategory(long categoryId) {
        log.info("Получаем категорию по её id.");
        return categoryRepository.findById(categoryId).orElseThrow(() ->
                new NotFoundException(String.format("Категория с id=%d не найдена.", categoryId)));
    }
}

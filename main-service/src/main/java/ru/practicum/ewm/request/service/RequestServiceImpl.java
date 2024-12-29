package ru.practicum.ewm.request.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.enums.EventStates;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConditionsNotMetException;
import ru.practicum.ewm.exception.DuplicatedDataException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.enums.RequestStatuses;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RequestServiceImpl implements RequestService {
    RequestRepository repository;
    UserRepository userRepository;
    EventRepository eventRepository;

    @Autowired
    public RequestServiceImpl(RequestRepository repository,
                              UserRepository userRepository,
                              EventRepository eventRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    @Transactional
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        log.info("Добавляем новый запрос на участие в событии.");
        // Проверяем существует ли пользователь и событие
        isUserExists(userId);
        Event event = findEventById(eventId);

        // Проверяем существует ли уже запрос на участие в данном событии от этого пользователя
        if (repository.findByRequesterAndEvent(userId, eventId).isPresent()) {
            throw new DuplicatedDataException(String.format("Не удалось добавить запрос на участие в событии id=%d" +
                    " от пользователя id=%d. Такой запрос уже существует.", eventId, eventId));
        }

        // Проверяем является ли инициатор запроса инициатором события
        if (userId.equals(event.getInitiator().getId())) {
            throw new ConditionsNotMetException("Ошибка при создании запроса на участие в событии. Инициатор запроса " +
                    "не может быть инициатором события.");
        }

        // Проверяем опубликовано ли событие
        if (!event.getState().equals(EventStates.PUBLISHED)) {
            throw new ConditionsNotMetException("Ошибка при создании запроса на участие в событии. Нельзя участвовать" +
                    " в неопубликованном событии.");
        }

        Request request = Request.builder()
                .id(null)
                .requester(userId)
                .event(eventId)
                .created(LocalDateTime.now())
                .build();

        /*Проверяем достигнут ли лимит запросов на участие
        Если ParticipantLimit не равно 0 (ограничения нет) и при этом число запросов на участие равно
        ParticipantLimit, то выкидываем исключение
        */
        if (event.getParticipantLimit() > 0) {
            if (repository.findAllByEventAndStatusIn(eventId, List.of(RequestStatuses.CONFIRMED)).size() == event.getParticipantLimit()) {
                throw new ConditionsNotMetException("Ошибка при создании запроса на участие в событии. Достигнут лимит " +
                        "запросов для этого события.");
            }

            if (event.isRequestModeration()) {
                request.setStatus(RequestStatuses.PENDING);
            } else {
                request.setStatus(RequestStatuses.CONFIRMED);
            }
        } else {
            request.setStatus(RequestStatuses.CONFIRMED);
        }

        Request savedRequest = repository.save(request);
        return RequestMapper.mapRequestToDto(savedRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getAllUserRequests(Long userId) {
        log.info("Получаем запросы на участие определенного пользователя.");
        // Проверяем существует ли пользователь
        isUserExists(userId);

        List<Request> result = repository.findAllByRequester(userId);
        if (result.isEmpty()) {
            return new ArrayList<>();
        } else {
            return result.stream()
                    .map(RequestMapper::mapRequestToDto)
                    .toList();
        }
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        log.info("Отменяем запрос на участие в событии.");

        // Проверяем существует ли пользователь и запрос
        isUserExists(userId);
        Request request = repository.findById(requestId).orElseThrow(() -> new NotFoundException(String.format("Не удалось отменить запрос на участие в событии. Запрос " +
                "с id=%d не найден.", requestId)));

        request.setStatus(RequestStatuses.CANCELED);

        Request canceledRequest = repository.save(request);
        return RequestMapper.mapRequestToDto(canceledRequest);

    }

    private void isUserExists(Long userId) {
        log.info("Проверяем существует ли пользователя по его id.");

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь с id=%d не найден.", userId));
        }
    }

    private Event findEventById(Long eventId) {
        log.info("Проверяем существует ли событие по id.");
        Optional<Event> event = eventRepository.findById(eventId);

        if (event.isEmpty()) {
            throw new NotFoundException(String.format("Событие с id=%d не найдено.", eventId));
        }

        return event.get();
    }
}

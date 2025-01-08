package ru.practicum.ewm.comment.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentRequest;
import ru.practicum.ewm.comment.enums.CommentStatuses;
import ru.practicum.ewm.comment.mapper.CommentMapper;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConditionsNotMetException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.enums.RequestStatuses;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PrivateCommentServiceImpl implements PrivateCommentService {
    CommentRepository repository;
    EventRepository eventRepository;
    UserRepository userRepository;
    RequestRepository requestRepository;

    @Override
    @Transactional
    public CommentDto createComment(NewCommentRequest request, Long userId, Long eventId) {
        log.info("Добавляем новый комментарий пользователя к событию.");

        // Проверяем существует ли событие и пользователь
        User user = getUser(userId);
        Event event = getEvent(eventId);

        // Если нет одобренного запроса на участие для этого пользователя, то выбрасываем исключение
        requestRepository.findByRequesterAndEventAndStatus(userId, eventId, RequestStatuses.CONFIRMED)
                .orElseThrow(() -> new ConditionsNotMetException("Оставить комментарий можно только на событие, в " +
                        "котором вам было одобрено участие."));

        // Если событие еще не состоялось, выбрасываем исключение
//        if (LocalDateTime.now().isBefore(event.getEventDate())) {
//            throw new ConditionsNotMetException("Нельзя оставить комментарий на событие, которое еще не состоялось.");
//        }

        Comment savedComment = repository.save(CommentMapper.mapNewRequestToComment(request, user, event));
        return CommentMapper.mapCommentToCommentDto(savedComment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getAllUserComments(Long userId, int from, int size) {
        log.info("Получаем список всех комментариев пользователя.");
        Pageable pageable = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "id"));
        List<Comment> comments = repository.findAllByAuthorId(userId, pageable);
        return comments.stream()
                .map(CommentMapper::mapCommentToCommentDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDto getCommentById(Long userId, Long commentId) {
        log.info("Получаем комментарий по его id и id автора.");
        return CommentMapper.mapCommentToCommentDto(getComment(commentId, userId));
    }

    // Обновляем текст комментария и меняем статус на PENDING
    @Override
    @Transactional
    public CommentDto updateComment(NewCommentRequest request, Long userId, Long commentId) {
        log.info("Обновляем данные комментария пользователя к событию.");

        // Проверяем есть ли у пользователя комментарий с таким id
        Comment comment = getComment(commentId, userId);

        comment.setText(request.getText());
        comment.setStatus(CommentStatuses.PENDING);
        Comment updatedComment = repository.save(comment);

        return CommentMapper.mapCommentToCommentDto(updatedComment);
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        log.info("Удаляем комментарий пользователя к событию.");

        // Проверяем есть ли у пользователя комментарий с таким id
        getComment(commentId, userId);

        repository.deleteById(commentId);
    }

    private Event getEvent(Long eventId) {
        log.info("Получаем событие по его id.");
        return eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Событие с id=%d не найдено.", eventId)));
    }

    private User getUser(Long userId) {
        log.info("Получаем пользователя по его id.");
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id=%d не найден.", userId)));
    }

    private Comment getComment(Long commentId, Long userId) {
        log.info("Получаем комментарий по его id.");
        return repository.findByIdAndAuthorId(commentId, userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%d никогда не добавлял " +
                        "комментария с id=%d.", userId, commentId)));
    }
}

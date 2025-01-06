package ru.practicum.ewm.comment.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.UpdateCommentAdminRequest;
import ru.practicum.ewm.comment.enums.CommentStatuses;
import ru.practicum.ewm.comment.enums.UpdateCommentAdminStatuses;
import ru.practicum.ewm.comment.mapper.CommentMapper;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.exception.ConditionsNotMetException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminCommentServiceImpl implements AdminCommentService {
    CommentRepository repository;

    @Override
    public List<CommentDto> getAllCommentsByCriteria(List<Long> users, List<Long> events, List<String> statuses,
                                                     LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from,
                                                     Integer size) {
        log.info("Получаем список всех комментариев по переданным условиям.");

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("Неверно переданы условия. Дата и время rangeStart не могут идти после " +
                    "rangeEnd.");
        }

        Specification<Comment> specification = Specification.where(null);
        if (users != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("author").get("id").in(users));
        }

        if (statuses != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("status").as(String.class).in(statuses));
        }

        if (events != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("event").get("id").in(events));
        }

        if (rangeStart != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("created"), rangeStart));
        }

        if (rangeEnd != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("created"), rangeEnd));
        }

        List<Comment> comments = repository.findAll(specification, PageRequest.of(from, size)).getContent();
        return comments.stream()
                .map(CommentMapper::mapCommentToCommentDto)
                .toList();
    }

    @Override
    public CommentDto updateCommentByAdmin(Long commentId, UpdateCommentAdminRequest request) {
        log.info("Обновляем комментарий по запросу администратора.");

        // Проверяем существует ли комментарий
        Comment comment = repository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format("Комментарий по id=%d не найден.", commentId)));

        if (!comment.getStatus().equals(CommentStatuses.PENDING) && request.getStatus()
                .equals(UpdateCommentAdminStatuses.PUBLISH_COMMENT)) {
            throw new ConditionsNotMetException("Комментарий можно опубликовать, только если он в состоянии ожидания " +
                    "публикации.");
        }

        if (comment.getStatus().equals(CommentStatuses.PUBLISHED) && request.getStatus()
                .equals(UpdateCommentAdminStatuses.REJECT_COMMENT)) {
            throw new ConditionsNotMetException("Комментарий можно отклонить, только если он еще не опубликован.");
        }

        Comment updatedComment = repository.save(CommentMapper.updateCommentDataByAdmin(comment, request));
        return CommentMapper.mapCommentToCommentDto(updatedComment);
    }
}

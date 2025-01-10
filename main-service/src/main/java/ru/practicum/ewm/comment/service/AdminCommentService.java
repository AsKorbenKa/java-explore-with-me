package ru.practicum.ewm.comment.service;

import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.UpdateCommentAdminRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminCommentService {
    List<CommentDto> getAllCommentsByCriteria(List<Long> users, List<Long> events, List<String> statuses,
                                              LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from,
                                              Integer size);

    CommentDto updateCommentByAdmin(Long commentId, UpdateCommentAdminRequest request);
}

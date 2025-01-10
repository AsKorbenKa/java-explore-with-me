package ru.practicum.ewm.comment.service;

import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentRequest;

import java.util.List;

public interface PrivateCommentService {
    CommentDto createComment(NewCommentRequest request, Long userId, Long eventId);

    List<CommentDto> getAllUserComments(Long userId, int from, int size);

    CommentDto getCommentById(Long userId, Long commentId);

    CommentDto updateComment(NewCommentRequest request, Long userId, Long commentId);

    void deleteComment(Long userId, Long commentId);
}

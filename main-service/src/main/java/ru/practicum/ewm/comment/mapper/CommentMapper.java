package ru.practicum.ewm.comment.mapper;

import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentRequest;
import ru.practicum.ewm.comment.dto.UpdateCommentAdminRequest;
import ru.practicum.ewm.comment.enums.CommentStatuses;
import ru.practicum.ewm.comment.enums.UpdateCommentAdminStatuses;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.dto.UserShortDto;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;

public class CommentMapper {
    public static Comment mapNewRequestToComment(NewCommentRequest request, User user, Event event) {
        return new Comment(
                null,
                request.getText(),
                user,
                event,
                CommentStatuses.PENDING,
                LocalDateTime.now()
        );
    }

    public static CommentDto mapCommentToCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                new UserShortDto(comment.getAuthor().getId(), comment.getAuthor().getName()),
                EventMapper.mapEventToEventShortDto(comment.getEvent()),
                comment.getStatus(),
                comment.getCreated()
        );
    }

    public static Comment updateCommentDataByAdmin(Comment comment, UpdateCommentAdminRequest request) {
        if (request.getText() != null) {
            comment.setText(request.getText());
        }

        if (request.getStatus().equals(UpdateCommentAdminStatuses.PUBLISH_COMMENT)) {
            comment.setStatus(CommentStatuses.PUBLISHED);
        } else if (request.getStatus().equals(UpdateCommentAdminStatuses.REJECT_COMMENT)) {
            comment.setStatus(CommentStatuses.REJECTED);
        }

        return comment;
    }
}

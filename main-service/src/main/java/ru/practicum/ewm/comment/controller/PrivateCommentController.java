package ru.practicum.ewm.comment.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentRequest;
import ru.practicum.ewm.comment.service.PrivateCommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/comments")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PrivateCommentController {
    PrivateCommentService service;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public CommentDto createComment(@Valid @RequestBody NewCommentRequest request,
                                    @PathVariable("userId") Long userId,
                                    @RequestParam(name = "eventId") Long eventId) {
        return service.createComment(request, userId, eventId);
    }

    @GetMapping
    public List<CommentDto> getAllUserComments(@PathVariable("userId") Long userId,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(defaultValue = "10") @Positive int size) {
        return service.getAllUserComments(userId, from, size);
    }

    @GetMapping("/{commentId}")
    public CommentDto getCommentById(@PathVariable("userId") Long userId,
                                     @PathVariable("commentId") Long commentId) {
        return service.getCommentById(userId, commentId);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(@Valid @RequestBody NewCommentRequest request,
                                    @PathVariable("userId") Long userId,
                                    @PathVariable("commentId") Long commentId) {
        return service.updateComment(request, userId, commentId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable("userId") Long userId,
                              @PathVariable("commentId") Long commentId) {
        service.deleteComment(userId, commentId);
    }
}

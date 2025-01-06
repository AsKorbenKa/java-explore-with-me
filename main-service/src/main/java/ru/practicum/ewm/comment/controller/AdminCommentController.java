package ru.practicum.ewm.comment.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.UpdateCommentAdminRequest;
import ru.practicum.ewm.comment.service.AdminCommentService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/comments")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminCommentController {
    AdminCommentService service;

    @GetMapping
    public List<CommentDto> getAllCommentsByCriteria(@RequestParam(required = false) List<Long> users,
                                                     @RequestParam(required = false) List<Long> events,
                                                     @RequestParam(required = false) List<String> statuses,
                                                     @RequestParam(required = false) @DateTimeFormat(pattern =
                                                             "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                     @RequestParam(required = false) @DateTimeFormat(pattern =
                                                             "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                     @RequestParam(value = "from", defaultValue = "0")
                                                         @PositiveOrZero Integer from,
                                                     @RequestParam(value = "size", defaultValue = "10")
                                                         @Positive Integer size) {
        return service.getAllCommentsByCriteria(users, events, statuses, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateCommentByAdmin(@PathVariable("commentId") Long commentId,
                                           @Valid @RequestBody UpdateCommentAdminRequest request) {
        return service.updateCommentByAdmin(commentId, request);
    }
}

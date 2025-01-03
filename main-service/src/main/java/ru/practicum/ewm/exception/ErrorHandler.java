package ru.practicum.ewm.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler({DuplicatedDataException.class, ConstraintViolationException.class})
    public ResponseEntity<ApiError> handleDuplicatedData(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiError.builder()
                        .status("CONFLICT")
                        .reason("Запрос не может быть выполнен из-за конфликта с текущим состоянием целевого ресурса.")
                        .message(e.getMessage())
                        .build()
                );
    }

    @ExceptionHandler({ValidationException.class, NumberFormatException.class})
    public ResponseEntity<ApiError> handleValidation(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiError.builder()
                        .status("BAD_REQUEST")
                        .reason("Запрос, отправленный пользователем на сервер, был неверным.")
                        .message(e.getMessage())
                        .build()
                );
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleNotFound(NotFoundException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiError.builder()
                        .status("NOT_FOUND")
                        .reason("Требуемый объект не был найден.")
                        .message(e.getMessage())
                        .build()
        );
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleConditionsNotMet(ConditionsNotMetException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiError.builder()
                        .status("FORBIDDEN")
                        .reason("Не выполнены условия для запрашиваемой операции.")
                        .message(e.getMessage())
                        .build()
        );
    }
}

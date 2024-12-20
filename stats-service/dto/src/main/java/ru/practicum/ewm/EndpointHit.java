package ru.practicum.ewm;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EndpointHit {
    @NotBlank(message = "Текст поля app не может быть пустым.")
    String app;
    @NotBlank(message = "Текст поля uri не может быть пустым.")
    String uri;
    @NotBlank(message = "Текст поля ip не может быть пустым.")
    String ip;
    String timestamp;
}

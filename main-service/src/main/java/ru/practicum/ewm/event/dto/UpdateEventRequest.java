package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class UpdateEventRequest {
    @Length(min = 20, max = 2000)
    String annotation;

    @Positive(message = "ID категории не может быть отрицательным числом")
    Long category;

    @Length(min = 20, max = 7000)
    String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;

    @Valid
    LocationDto location;

    Boolean paid;

    @PositiveOrZero(message = "Ограничение на количество участников не может быть отрицательным числом")
    Long participantLimit;

    Boolean requestModeration;

    @Length(min = 3, max = 120)
    String title;
}

package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventDto {
    @NotBlank(message = "Поле annotation не может быть пустым.")
    @Length(min = 20, max = 2000)
    String annotation;

    @Positive(message = "Id категории не может быть меньше или равно нулю.")
    Long category;

    @NotBlank(message = "Поле description не может быть пустым.")
    @Length(min = 20, max = 7000)
    String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    String eventDate;

    @NotNull
    @Valid
    LocationDto location;

    Boolean paid;

    @PositiveOrZero(message = "Значение поля participantLimit не может быть меньше нуля.")
    Long participantLimit;

    Boolean requestModeration;

    @NotBlank(message = "Поле title не может быть пустым.")
    @Length(min = 3, max = 120)
    String title;
}

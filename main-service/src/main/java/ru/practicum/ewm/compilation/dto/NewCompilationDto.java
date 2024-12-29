package ru.practicum.ewm.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCompilationDto {
    Set<Long> events;
    boolean pinned;

    @NotBlank(message = "Заголовок подборки событий не может быть пустым.")
    @Length(min = 1, max = 50)
    String title;
}

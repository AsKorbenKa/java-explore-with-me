package ru.practicum.ewm.compilation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.event.dto.EventShortDto;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long id;

    Set<EventShortDto> events;

    Boolean pinned;

    String title;
}

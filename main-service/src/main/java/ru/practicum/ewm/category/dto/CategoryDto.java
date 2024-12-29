package ru.practicum.ewm.category.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long id;
    String name;
}

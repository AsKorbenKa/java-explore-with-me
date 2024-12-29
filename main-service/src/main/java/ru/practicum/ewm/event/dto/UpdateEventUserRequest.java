package ru.practicum.ewm.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.event.enums.UpdateEventUserStates;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventUserRequest extends UpdateEventRequest {
    UpdateEventUserStates stateAction;
}

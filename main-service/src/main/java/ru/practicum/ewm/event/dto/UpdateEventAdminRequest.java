package ru.practicum.ewm.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.event.enums.UpdateEventAdminStates;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventAdminRequest extends UpdateEventRequest {
    UpdateEventAdminStates stateAction;
}

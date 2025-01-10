package ru.practicum.ewm.comment.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;
import ru.practicum.ewm.comment.enums.UpdateCommentAdminStatuses;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateCommentAdminRequest {
    @Length(min = 20, max = 2000)
    String text;
    UpdateCommentAdminStatuses status;
}

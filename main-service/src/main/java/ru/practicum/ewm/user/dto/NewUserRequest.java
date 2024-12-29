package ru.practicum.ewm.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class NewUserRequest {
    @NotBlank(message = "Имя пользователя не может быть пустым.")
    @Length(min = 2, max = 250)
    String name;
    @Email(message = "Email должен быть в формате user@yandex.ru.")
    @NotBlank(message = "Email пользователя не может быть пустым.")
    @Length(min = 6, max = 254)
    String email;
}

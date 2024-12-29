package ru.practicum.ewm.user.mapper;

import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.model.User;

public class UserMapper {
    public static User mapUserRequestToUser(NewUserRequest newUserRequest) {
        return new User(
                null,
                newUserRequest.getName(),
                newUserRequest.getEmail()
        );
    }

    public static UserDto mapUserToUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail());
    }
}

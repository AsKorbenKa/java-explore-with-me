package ru.practicum.ewm.user.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.DuplicatedDataException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository repository;

    @Autowired
    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public UserDto createUser(NewUserRequest newUserRequest) {
        log.info("Добавляем нового пользователя в базу данных.");

        // Проверяем поле name на валидность
        if (newUserRequest.getName().isBlank()) {
            throw new ValidationException(String.format("Поле: name. Ошибка: не может быть пустым. Значение: %s",
                    newUserRequest.getName()));
        }

        // Проверяем существует ли пользователь с email нового пользователя
        Optional<User> result = repository.findByEmail(newUserRequest.getEmail());
        if (result.isPresent()) {
            throw new DuplicatedDataException(String.format("Ошибка при добавлении нового пользователя. Пользователь " +
                    "с email %s уже существует.", newUserRequest.getEmail()));
        }

        User savedUser = repository.save(UserMapper.mapUserRequestToUser(newUserRequest));
        return UserMapper.mapUserToUserDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Integer> ids, int from, int size) {
        log.info("Получаем данные пользователей по параметрам.");
        List<User> users;
        if (ids != null) {
            log.info("Получаем пользователей по их id.");
            users = repository.findAllByIdIn(ids);
        } else {
            log.info("Получаем всех пользователей, используя пагинацию");
            Pageable pageable = PageRequest.of(from, size, Sort.by(Sort.Direction.ASC, "id"));
            users = repository.findAll(pageable).stream().toList();
        }

        return users.stream()
                .map(UserMapper::mapUserToUserDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        log.info("Удаляем пользователя по его id.");

        // Проверяем существует ли пользователь с таким id
        if (!repository.existsById(userId)) {
            throw new NotFoundException(String.format("Ошибка при удалении пользователя. " +
                    "Пользователь с id=%d не найден.", userId));
        }

        repository.deleteById(userId);
    }


}

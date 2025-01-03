package ru.practicum.ewm.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    List<User> findAllByIdIn(List<Integer> ids);
}

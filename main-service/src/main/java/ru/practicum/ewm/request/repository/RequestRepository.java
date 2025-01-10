package ru.practicum.ewm.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.request.enums.RequestStatuses;
import ru.practicum.ewm.request.model.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Optional<Request> findByRequesterAndEvent(Long requester, Long event);

    Optional<Request> findByRequesterAndEventAndStatus(Long requester, Long event, RequestStatuses status);

    List<Request> findAllByEvent(Long event);

    List<Request> findAllByRequester(Long event);

    List<Request> findAllByEventAndStatusIn(Long event, List<RequestStatuses> statuses);

    List<Request> findAllByEventAndStatus(Long event, RequestStatuses status);

    List<Request> findAllByIdIn(List<Long> requestIds);
}

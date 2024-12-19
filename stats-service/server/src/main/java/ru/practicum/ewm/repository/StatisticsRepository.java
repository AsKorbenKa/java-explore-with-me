package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.ViewStats;
import ru.practicum.ewm.model.EndpointHitModel;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticsRepository  extends JpaRepository<EndpointHitModel, Long> {
    @Query("select e.app as app, e.uri as uri, count(e.ip) as hits " +
            "from EndpointHitModel as e " +
            "where e.uri in ?1 " +
            "and e.timestamp >= ?2 " +
            "and e.timestamp <= ?3 " +
            "group by e.app, e.uri " +
            "order by hits DESC")
    List<ViewStats> findAllByUriAndTimestampAfterAndTimestampBefore(List<String> uri, LocalDateTime start,
                                                                           LocalDateTime end);

    @Query("select e.app as app, e.uri as uri, count(e.ip) as hits " +
            "from EndpointHitModel as e " +
            "where e.timestamp >= ?1 " +
            "and e.timestamp <= ?2 " +
            "group by e.app, e.uri " +
            "order by hits DESC")
    List<ViewStats> findAllByTimestampAfterAndTimestampBefore(LocalDateTime start,
                                                                    LocalDateTime end);

    @Query("select e.app as app, e.uri as uri, count(distinct e.ip) as hits " +
            "from EndpointHitModel as e " +
            "where e.uri in ?1 " +
            "and e.timestamp >= ?2 " +
            "and e.timestamp <= ?3 " +
            "group by e.app, e.uri " +
            "order by hits DESC")
    List<ViewStats> findDistinctIpByUriAndTimestampAfterAndTimestampBefore(List<String> uri, LocalDateTime start,
                                                                                LocalDateTime end);

    @Query("select e.app as app, e.uri as uri, count(distinct e.ip) as hits " +
            "from EndpointHitModel as e " +
            "where e.timestamp >= ?1 " +
            "and e.timestamp <= ?2 " +
            "group by e.app, e.uri " +
            "order by hits DESC")
    List<ViewStats> findDistinctIpByTimestampAfterAndTimestampBefore(LocalDateTime start,
                                                                           LocalDateTime end);
}

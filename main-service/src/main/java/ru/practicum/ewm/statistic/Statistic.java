package ru.practicum.ewm.statistic;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.EndpointHit;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.ViewStatsDto;
import ru.practicum.ewm.event.model.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
public class Statistic {
    StatsClient statsClient;
    String serviceName;

    @Autowired
    public Statistic(StatsClient statsClient, @Value("${ewm.service.name}") String serviceName) {
        this.statsClient = statsClient;
        this.serviceName = serviceName;
    }

    private List<Long> getDynamicPartOfUri(ViewStatsDto statsDto) {
        if (!statsDto.getUri().isBlank()) {
            return Arrays.stream(statsDto.getUri().split("/"))
                    .skip(1L)
                    .filter(elem -> elem.chars().allMatch(Character::isDigit))
                    .map(Long::valueOf)
                    .toList();
        }

        return List.of();
    }

    private List<String> createUris(List<Event> events) {
        return events.stream().map(elem -> String.format("/events/%d", elem.getId())).toList();
    }

    private LocalDateTime findStartDate(List<Event> events) {
        return events.stream()
                .map(Event::getPublishedOn)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.of(1900, 1, 1, 0, 0, 0));
    }

    private LocalDateTime findEndDate(List<Event> events) {
        return events.stream()
                .map(Event::getEventDate)
                .max(LocalDateTime::compareTo)
                .orElse(LocalDateTime.of(9999, 12, 31, 23, 59, 59));
    }

    private Map<Long, Long> findStatistic(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        return statsClient.getStats(start, end, uris, unique)
                .stream()
                .collect(Collectors.toMap(elem -> getDynamicPartOfUri(elem).getFirst(), ViewStatsDto::getHits));
    }

    public Map<Long, Long> getStatistic(List<Event> events, Boolean unique) {
        LocalDateTime rangeStart = findStartDate(events);
        LocalDateTime rangeEnd = findEndDate(events);
        List<String> uris = createUris(events);

        return findStatistic(rangeStart, rangeEnd, uris, unique);
    }

    public Map<Long, Long> getStatistic(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        return findStatistic(start, end, uris, unique);
    }

    public void saveStatistic(HttpServletRequest request) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        EndpointHit dto = new EndpointHit(
                serviceName,
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now().format(format)
        );

        statsClient.createStat(dto);
    }
}

package ru.practicum.ewm.service;

import ru.practicum.ewm.EndpointHit;
import ru.practicum.ewm.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticsService {
    void createStats(EndpointHit endpointHit);

    List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}

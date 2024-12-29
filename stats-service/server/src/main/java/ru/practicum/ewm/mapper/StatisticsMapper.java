package ru.practicum.ewm.mapper;

import ru.practicum.ewm.EndpointHit;
import ru.practicum.ewm.ViewStats;
import ru.practicum.ewm.ViewStatsDto;
import ru.practicum.ewm.model.EndpointHitModel;

import java.time.LocalDateTime;

public class StatisticsMapper {
    public static EndpointHitModel mapHitToHitModel(EndpointHit endpointHit) {
        return new EndpointHitModel(
                null,
                endpointHit.getApp(),
                endpointHit.getUri(),
                endpointHit.getIp(),
                LocalDateTime.parse(endpointHit.getTimestamp())
        );
    }

    public static ViewStatsDto mapViewStatsToDto(ViewStats viewStats) {
        return new ViewStatsDto(viewStats.getApp(), viewStats.getUri(), (long) viewStats.getHits());
    }
}

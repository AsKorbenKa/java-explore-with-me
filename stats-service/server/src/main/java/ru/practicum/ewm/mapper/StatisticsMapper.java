package ru.practicum.ewm.mapper;

import ru.practicum.ewm.EndpointHit;
import ru.practicum.ewm.ViewStats;
import ru.practicum.ewm.ViewStatsDto;
import ru.practicum.ewm.model.EndpointHitModel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StatisticsMapper {
    public static EndpointHitModel mapHitToHitModel(EndpointHit endpointHit) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime result = LocalDateTime.parse(endpointHit.getTimestamp(), format);

        return new EndpointHitModel(
                null,
                endpointHit.getApp(),
                endpointHit.getUri(),
                endpointHit.getIp(),
                result
        );
    }

    public static ViewStatsDto mapViewStatsToDto(ViewStats viewStats) {
        return new ViewStatsDto(viewStats.getApp(), viewStats.getUri(), (long) viewStats.getHits());
    }
}

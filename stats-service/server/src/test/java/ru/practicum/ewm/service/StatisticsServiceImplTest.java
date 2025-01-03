package ru.practicum.ewm.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.ewm.EndpointHit;
import ru.practicum.ewm.ViewStats;
import ru.practicum.ewm.ViewStatsDto;
import ru.practicum.ewm.mapper.StatisticsMapper;
import ru.practicum.ewm.model.EndpointHitModel;
import ru.practicum.ewm.repository.StatisticsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceImplTest {
    private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final LocalDateTime start = LocalDateTime.parse("2022-09-06 10:00:23", format);
    private final LocalDateTime end = LocalDateTime.parse("2022-09-07 12:00:23", format);
    @Mock
    StatisticsRepository repository;

    @InjectMocks
    StatisticsServiceImpl statisticsService;

    private final EndpointHit endpointHit = new EndpointHit(
            "ewm-main-service",
            "/events/1",
            "192.163.0.1",
            "2022-09-06 11:00:23"
    );

    private final ViewStats viewStats = new ViewStats() {
        @Override
        public String getApp() {
            return "ewm-main-service";
        }

        @Override
        public String getUri() {
            return "/events/1";
        }

        @Override
        public int getHits() {
            return 5;
        }
    };

    private final ViewStatsDto viewStatsDto = new ViewStatsDto(
            "ewm-main-service",
            "/events/1",
            5L
    );

    @Test
    void createStats() {
        EndpointHitModel endpointHitModel = StatisticsMapper.mapHitToHitModel(endpointHit);
        when(repository.save(any(EndpointHitModel.class))).thenReturn(endpointHitModel);

        statisticsService.createStats(endpointHit);
        verify(repository, times(1)).save(any(EndpointHitModel.class));
    }

    @Test
    void getStatsWhenIpIsNotUniqueAndUrisIsNotEmpty() {
        when(repository.findAllByUriAndTimestampAfterAndTimestampBefore(List.of(endpointHit.getUri()), start, end))
                .thenReturn(List.of(viewStats));

        List<ViewStatsDto> result = statisticsService.getStats(start, end, List.of("/events/1"), false);

        assertEquals(result, List.of(viewStatsDto));
    }

    @Test
    void getStatsWhenIpIsUniqueAndUrisIsNotEmpty() {
        when(repository.findDistinctIpByUriAndTimestampAfterAndTimestampBefore(List.of(endpointHit.getUri()), start, end))
                .thenReturn(List.of(viewStats));

        List<ViewStatsDto> result = statisticsService.getStats(start, end, List.of("/events/1"), true);

        assertEquals(result, List.of(viewStatsDto));
    }

    @Test
    void getStatsWhenIpIsNotUniqueAndUrisIsEmpty() {
        when(repository.findAllByTimestampAfterAndTimestampBefore(start, end))
                .thenReturn(List.of(viewStats));

        List<ViewStatsDto> result = statisticsService.getStats(start, end, new ArrayList<>(), false);

        assertEquals(result, List.of(viewStatsDto));
    }

    @Test
    void getStatsWhenIpIsUniqueAndUrisIsEmpty() {
        when(repository.findDistinctIpByTimestampAfterAndTimestampBefore(start, end))
                .thenReturn(List.of(viewStats));

        List<ViewStatsDto> result = statisticsService.getStats(start, end, new ArrayList<>(), true);

        assertEquals(result, List.of(viewStatsDto));
    }
}
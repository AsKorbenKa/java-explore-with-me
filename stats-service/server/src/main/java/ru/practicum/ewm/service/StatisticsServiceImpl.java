package ru.practicum.ewm.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.EndpointHit;
import ru.practicum.ewm.ViewStatsDto;
import ru.practicum.ewm.mapper.StatisticsMapper;
import ru.practicum.ewm.model.EndpointHitModel;
import ru.practicum.ewm.repository.StatisticsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticsServiceImpl implements StatisticsService {
    StatisticsRepository repository;

    @Autowired
    public StatisticsServiceImpl(StatisticsRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void createStats(EndpointHit endpointHit) {
        log.info("Добавляем новые данные статистики.");
        EndpointHitModel newEndpointHit = StatisticsMapper.mapHitToHitModel(endpointHit);
        repository.save(newEndpointHit);
        log.info("Новые данные статистики успешно добавлены.");
    }

    // получаем статистику в зависимости от того, пуст ли список uris и чему равно поле unique
    @Override
    @Transactional(readOnly = true)
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        log.info("Получаем данные статистики.");

        if (uris.isEmpty()) {
            if (unique) {
                log.info("Получаем данные статистики за определенное время c уникальными значениями ip.");
                return repository.findDistinctIpByTimestampAfterAndTimestampBefore(start, end).stream()
                        .map(StatisticsMapper::mapViewStatsToDto)
                        .toList();
            } else {
                log.info("Получаем все данные статистики за определенное время.");
                return repository.findAllByTimestampAfterAndTimestampBefore(start, end).stream()
                        .map(StatisticsMapper::mapViewStatsToDto)
                        .toList();
            }
        } else {
            if (unique) {
                log.info("Получаем данные статистики по uri за определенное время c уникальными значениями ip.");
                return repository.findDistinctIpByUriAndTimestampAfterAndTimestampBefore(uris, start, end).stream()
                        .map(StatisticsMapper::mapViewStatsToDto)
                        .toList();
            } else {
                log.info("Получаем все данные статистики по uri за определенное время.");
                return repository.findAllByUriAndTimestampAfterAndTimestampBefore(uris, start, end).stream()
                        .map(StatisticsMapper::mapViewStatsToDto)
                        .toList();
            }
        }
    }
}

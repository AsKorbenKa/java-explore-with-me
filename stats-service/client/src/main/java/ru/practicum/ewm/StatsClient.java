package ru.practicum.ewm;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class StatsClient extends BaseClient {
    ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Autowired
    public StatsClient(@Value("${stats.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public void createStat(EndpointHit endpointHitDto) {
        post(endpointHitDto);
    }

    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        String urisString = String.join(", ", uris);

        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", urisString,
                "unique", unique
        );

        ResponseEntity<Object> response = get("/stats?start={start}&end={end}&uris={uris}&unique={unique}",
                parameters);
        return mapper.convertValue(response.getBody(), new TypeReference<>() {
        });
    }
}

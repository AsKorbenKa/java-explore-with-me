package ru.practicum.ewm.statistic;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.ewm.StatsClient;

@Configuration
public class StatsClientConfig {

    @Value("${stats.url}")
    private String url;

    @Bean
    StatsClient statsClient() {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        return new StatsClient(url, builder);
    }
}

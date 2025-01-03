package ru.practicum.ewm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.EndpointHit;
import ru.practicum.ewm.ViewStats;
import ru.practicum.ewm.ViewStatsDto;
import ru.practicum.ewm.service.StatisticsService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StatisticsController.class)
class StatisticsControllerTest {
    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private StatisticsService service;

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
    @SneakyThrows
    void createStats() {
        mvc.perform(post("/hit")
                        .content(mapper.writeValueAsString(endpointHit))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @SneakyThrows
    void getStats() {
        when(service.getStats(any(LocalDateTime.class), any(LocalDateTime.class), any(), any(boolean.class)))
                .thenReturn(List.of(viewStatsDto));

        String result = mvc.perform(get("/stats")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("start", "2022-09-06 10:00:23")
                        .param("end", "2022-09-07 12:00:23")
                        .param("uris", String.valueOf(List.of("/events/1")))
                        .param("unique", String.valueOf(false)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(result, mapper.writeValueAsString(List.of(viewStatsDto)));
    }

    @Test
    @SneakyThrows
    void getStatsWhenUrisListIsEmpty() {
        String result = mvc.perform(get("/stats")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("start", "2022-09-06 10:00:23")
                        .param("end", "2022-09-07 12:00:23")
                        .param("uris", String.valueOf(new ArrayList<>()))
                        .param("unique", String.valueOf(false)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(result, mapper.writeValueAsString(new ArrayList<>()));
    }

    @Test
    @SneakyThrows
    void getStatsWhenStartGreaterThanEndThenThrowValidationException() {
        mvc.perform(get("/stats")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("start", "2022-09-07 12:00:23")
                        .param("end", "2022-09-06 10:00:23")
                        .param("uris", String.valueOf(List.of("/events/1")))
                        .param("unique", String.valueOf(false)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Ошибка в проверяемых данных.")))
                .andExpect(jsonPath("$.description", is("Ошибка при получении данных статистики. " +
                        "Дата и время end не могут идти после start.")));
    }
}
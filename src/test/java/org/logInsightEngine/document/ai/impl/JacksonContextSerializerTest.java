package org.logInsightEngine.document.ai.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.logInsightEngine.dtos.context.AIAnalysisContext;
import org.logInsightEngine.dtos.result.*;
import org.logInsightEngine.model.domain.LogLevel;

import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JacksonContextSerializerTest {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS);

    private final JacksonContextSerializer jacksonContextSerializer = new JacksonContextSerializer(objectMapper);


    @Test
    void testNull() {
        assertThrows(NullPointerException.class, () -> jacksonContextSerializer.serialize(null));
    }

    @Test
    void shouldSerializeInstant() throws Exception {
        String context = objectMapper.writeValueAsString(Instant.parse("2026-09-19T10:00:00Z"));
        assertEquals("\"2026-09-19T10:00:00Z\"", context);
    }

    @Test
    void testValidString() throws JsonProcessingException {

        AIAnalysisContext aiAnalysisContext = AIAnalysisContext.builder().summary(Summary.builder().totalLogEntries(7).firstTimestamp(Instant.parse("2026-09-19T10:00:00Z")).lastTimestamp(Instant.parse("2026-09-19T10:02:15Z")).duration(Duration.ofMinutes(2).plusSeconds(15)).build()).severityStatistics(SeverityStatistics.builder().countByLevel(new EnumMap<>(Map.of(LogLevel.DEBUG, 0L, LogLevel.INFO, 2L, LogLevel.WARN, 1L, LogLevel.ERROR, 3L, LogLevel.TRACE, 0L, LogLevel.ALL, 0L, LogLevel.FATAL, 0L, LogLevel.UNKNOWN, 1L))).build()).errorGroups(List.of(ErrorGroup.builder().id("error-group-1").fingerprint("java.sql.SQLException|Connection refused").exceptionType("java.sql.SQLException").message("Connection refused").occurrenceCount(2).firstOccurrence(Instant.parse("2026-09-19T10:01:00Z")).lastOccurrence(Instant.parse("2026-09-19T10:01:13Z")).impact(Impact.builder().threads(Set.of("http-nio-8080-exec-1")).loggers(Set.of("com.example.OrderService")).services(Set.of("order-service")).build()).build(), ErrorGroup.builder().id("error-group-2").fingerprint("java.net.SocketTimeoutException|Read timed out").exceptionType("java.net.SocketTimeoutException").message("Read timed out").occurrenceCount(1).firstOccurrence(Instant.parse("2026-09-19T10:01:13Z")).lastOccurrence(Instant.parse("2026-09-19T10:01:13Z")).impact(Impact.builder().threads(Set.of("http-nio-8080-exec-2")).loggers(Set.of("com.example.PaymentService")).services(Set.of("payment-service")).build()).build())).findings(List.of(Finding.builder().id("finding-1").priorityLevel(PriorityLevel.HIGH).category(FindingCategory.ERROR).title("Database connection failure").description("The application encountered database connection failures.").occurrenceCount(2).firstOccurrence(Instant.parse("2026-09-19T10:01:00Z")).lastOccurrence(Instant.parse("2026-09-19T10:01:13Z")).impact(Impact.builder().threads(Set.of("http-nio-8080-exec-1")).loggers(Set.of("com.example.OrderService")).services(Set.of("order-service")).build()).relatedErrorGroupIds(List.of("error-group-1")).build(), Finding.builder().id("finding-2").priorityLevel(PriorityLevel.HIGH).category(FindingCategory.ERROR).title("External service timeout").description("An external service request timed out.").occurrenceCount(1).firstOccurrence(Instant.parse("2026-09-19T10:01:13Z")).lastOccurrence(Instant.parse("2026-09-19T10:01:13Z")).impact(Impact.builder().threads(Set.of("http-nio-8080-exec-2")).loggers(Set.of("com.example.PaymentService")).services(Set.of("payment-service")).build()).relatedErrorGroupIds(List.of("error-group-2")).build())).correlations(List.of(Correlation.builder().id("correlation-1").targetId("error-group-2").targetType(CorrelationEntityType.ERROR_GROUP).sourceId("error-group-1").sourceType(CorrelationEntityType.ERROR_GROUP).relationshipType(CorrelationType.PRECEDES).timeDifference(Duration.ofSeconds(13)).build())).timeline(Timeline.builder().events(List.of(TimelineEvent.builder().timestamp(Instant.parse("2026-09-19T10:00:00Z")).level(LogLevel.INFO).title("Application started").description("Application started successfully.").findingId(null).build(), TimelineEvent.builder().timestamp(Instant.parse("2026-09-19T10:00:30Z")).level(LogLevel.WARN).title("High database connection usage").description("Database connection pool usage is approaching its configured limit.").findingId(null).build(), TimelineEvent.builder().timestamp(Instant.parse("2026-09-19T10:01:00Z")).level(LogLevel.ERROR).title("Database connection failure").description("The application encountered database connection failures.").findingId("finding-1").build(), TimelineEvent.builder().timestamp(Instant.parse("2026-09-19T10:01:13Z")).level(LogLevel.ERROR).title("External service timeout").description("An external service request timed out.").findingId("finding-2").build())).build()).build();
        String actual = jacksonContextSerializer.serialize(aiAnalysisContext);
        JsonNode actualJson = objectMapper.readTree(actual);
        assertEquals(7, actualJson.get("summary").get("totalLogEntries").asInt());
        assertEquals("2026-09-19T10:00:00Z", actualJson.get("summary").get("firstTimestamp").asText());
        assertEquals("2026-09-19T10:02:15Z", actualJson.get("summary").get("lastTimestamp").asText());
        assertEquals("PT2M15S", actualJson.get("summary").get("duration").asText());
        assertEquals(2, actualJson.get("errorGroups").size());
        assertEquals(2, actualJson.get("findings").size());
        assertEquals(1, actualJson.get("correlations").size());
        assertEquals(4, actualJson.get("timeline").get("events").size());
    }
}

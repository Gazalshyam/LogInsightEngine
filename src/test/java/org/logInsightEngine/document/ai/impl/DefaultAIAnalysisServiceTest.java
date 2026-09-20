package org.logInsightEngine.document.ai.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.logInsightEngine.document.ai.client.FakeLLMClient;
import org.logInsightEngine.document.ai.client.LLMClient;
import org.logInsightEngine.document.ai.service.AIContextBuilder;
import org.logInsightEngine.document.ai.service.AIContextSerializer;
import org.logInsightEngine.document.ai.service.AIResponseParser;
import org.logInsightEngine.document.ai.service.PromptBuilder;
import org.logInsightEngine.dtos.result.*;
import org.logInsightEngine.model.domain.LogLevel;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultAIAnalysisServiceTest {

    private DefaultAIAnalysisService service;

    @BeforeEach
    void setUp() {
        // real components
        AIContextBuilder contextBuilder = new DefaultAIContextBuilder();

        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS);
        AIContextSerializer contextSerializer = new JacksonContextSerializer(objectMapper);
        PromptBuilder promptBuilder = new DefaultPromptBuilder();

        String fakeResponse = """
                {
                  "summary": "The application experienced database connection failures.",
                  "probableRootCause": "Database connectivity failure.",
                  "contributingFactors": [
                    "Database connection errors occurred repeatedly."
                  ],
                  "affectedComponents": [
                    "order-service"
                  ],
                  "recommendedInvestigations": [
                    "Investigate database availability and connection pool configuration."
                  ],
                  "caveats": [
                    "The available evidence does not prove the underlying database cause."
                  ]
                }
                """;

        LLMClient llmClient = new FakeLLMClient(fakeResponse);
        AIResponseParser responseParser = new JacksonResponseParser(objectMapper);
        service = new DefaultAIAnalysisService(contextBuilder, contextSerializer, promptBuilder, llmClient, responseParser);
    }

    private AnalysisResult createAnalysisResult() {

        Instant firstOccurrence = Instant.parse("2026-09-19T10:01:00Z");
        Instant lastOccurrence = Instant.parse("2026-09-19T10:01:13Z");
        Impact impact = Impact.builder().threads(Set.of("http-nio-8080-exec-1")).loggers(Set.of("com.example.OrderService")).services(Set.of("order-service")).build();

        ErrorGroup errorGroup = ErrorGroup.builder().id("error-group-1").fingerprint("java.sql.SQLException|Connection refused").exceptionType("java.sql.SQLException").message("Connection refused").occurrenceCount(2).firstOccurrence(firstOccurrence).lastOccurrence(lastOccurrence).impact(impact).build();

        Finding finding = Finding.builder().id("finding-1").priorityLevel(PriorityLevel.HIGH).category(FindingCategory.ERROR).title("Database connection failure").description("The application encountered database connection failures.").occurrenceCount(2).firstOccurrence(firstOccurrence).lastOccurrence(lastOccurrence).impact(impact).relatedErrorGroupIds(List.of("error-group-1")).build();

        Summary summary = Summary.builder().totalLogEntries(3).firstTimestamp(Instant.parse("2026-09-19T10:00:00Z")).lastTimestamp(lastOccurrence).duration(Duration.ofSeconds(73)).build();

        SeverityStatistics severityStatistics = SeverityStatistics.builder().countByLevel(Map.of(LogLevel.INFO, 1L, LogLevel.WARN, 0L, LogLevel.ERROR, 2L)).build();

        TimelineEvent timelineEvent = TimelineEvent.builder().timestamp(firstOccurrence).level(LogLevel.ERROR).title("Database connection failure").description("The application encountered database connection failures.").findingId("finding-1").build();

        Timeline timeline = Timeline.builder().events(List.of(timelineEvent)).build();

        return AnalysisResult.builder().summary(summary).severityStatistics(severityStatistics).errorGroups(List.of(errorGroup)).findings(List.of(finding)).correlations(List.of()).timeline(timeline).build();
    }

    @Test
    void shouldRunCompleteAIAnalysisFlow() {

        AnalysisResult analysisResult = createAnalysisResult();
        AIAnalysisResult result = service.analyze(analysisResult);

        assertNotNull(result);
        assertEquals("The application experienced database connection failures.", result.getSummary());

        assertEquals("Database connectivity failure.", result.getProbableRootCause());
        assertEquals(List.of("order-service"), result.getAffectedComponents());
        assertFalse(result.getContributingFactors().isEmpty());
        assertFalse(result.getRecommendedInvestigations().isEmpty());
        assertFalse(result.getCaveats().isEmpty());
    }
}

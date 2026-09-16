package org.logInsightEngine.document.analyzer.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.logInsightEngine.document.analyzer.*;
import org.logInsightEngine.dtos.result.AnalysisResult;
import org.logInsightEngine.dtos.result.Correlation;
import org.logInsightEngine.dtos.result.CorrelationType;
import org.logInsightEngine.model.domain.LogEntry;
import org.logInsightEngine.model.domain.LogLevel;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DefaultLogAnalyzerIntegrationTest {


    private DefaultLogAnalyzer defaultLogAnalyzer ;
    @BeforeEach
    void setUp() {
        SummaryAnalyzer summaryAnalyzer = new DefaultSummaryAnalyzer();
        SeverityAnalyzer severityAnalyzer = new DefaultSeverityAnalyzer();
        ErrorAnalyzer errorAnalyzer = new DefaultErrorAnalyzerImplementation();
        FindingAnalyzer findingAnalyzer = new DefaultFindingAnalyzer();
        CorrelationAnalyzer correlationAnalyzer = new DefaultCorrelationAnalyzer();
        TimelineAnalyzer timelineAnalyzer = new DefaultTimelineAnalyzer();

        defaultLogAnalyzer = new DefaultLogAnalyzer(summaryAnalyzer, severityAnalyzer, errorAnalyzer, findingAnalyzer, timelineAnalyzer,  correlationAnalyzer);
    }

    private LogEntry createLogEntry(LogLevel level, String logger, String message, String thread, Instant timestamp, String stackTrace) {
        LogEntry logEntry = new LogEntry();
        logEntry.setLevel(level);
        logEntry.setLogger(logger);
        logEntry.setMessage(message);
        logEntry.setThread(thread);
        logEntry.setTimestamp(timestamp);
        logEntry.setStackTrace(stackTrace);
        return logEntry;
    }
    private List<LogEntry> createRealisticLogEntries() {
        return List.of(
                // INFO — should appear in Timeline
                createLogEntry(LogLevel.INFO, "Application", "Application started successfully", "main", Instant.ofEpochSecond(100), null),
                // INFO — should appear in Timeline
                createLogEntry(LogLevel.INFO,"AuthService", "User authentication request received", "auth-thread-1", Instant.ofEpochSecond(105), null),
                // WARN — should appear in Timeline
                createLogEntry(LogLevel.WARN, "DatabaseService", "Database connection retry initiated", "db-thread-1",Instant.ofEpochSecond(110), null),
                // ERROR — should become an ErrorGroup + Finding
                createLogEntry(LogLevel.ERROR, "DatabaseService", "Unable to acquire JDBC connection", "db-thread-1", Instant.ofEpochSecond(112),"java.sql.SQLException: Unable to acquire JDBC connection\n at com.example.DatabaseService.connect(DatabaseService.java:42)"),

                createLogEntry(LogLevel.ERROR, "DatabaseService", "Unable to acquire JDBC connection", "db-thread-1",Instant.ofEpochSecond(122),"java.sql.SQLException: Unable to acquire JDBC connection\n    at com.example.DatabaseService.connect(DatabaseService.java:42)" ),

                createLogEntry(LogLevel.ERROR, "PaymentService", "Connection timed out", "db-thread-1", Instant.ofEpochSecond(125),"java.net.SocketTimeoutException: Connection timed out\n   at com.example.PaymentService.call(PaymentService.java:87)"),
                // DEBUG — should be excluded from Timeline
                createLogEntry(LogLevel.DEBUG,"DatabaseService", "Connection pool debug information", "db-thread-1", Instant.ofEpochSecond(130), null)
        );
    }

    @Test
    public void shouldAnalyzeLogsEndToEnd() {
        List<LogEntry> logEntries = createRealisticLogEntries();
        AnalysisResult result = defaultLogAnalyzer.analyze(logEntries);
        assertNotNull(result);
        assertNotNull(result.getSummary());
        assertNotNull(result.getSeverityStatistics());
        assertNotNull(result.getErrorGroups());
        assertNotNull(result.getFindings());
        assertNotNull(result.getCorrelations());
        assertNotNull(result.getTimeline());
        assertEquals(Instant.ofEpochSecond(100), result.getTimeline().getEvents().get(0).getTimestamp());
        assertEquals(Instant.ofEpochSecond(125), result.getTimeline().getEvents().get(4).getTimestamp());
        assertEquals(7, result.getSummary().getTotalLogEntries());
        assertEquals(2, result.getErrorGroups().size());
        assertEquals(2, result.getFindings().size());
        assertEquals(5, result.getTimeline().getEvents().size());
        assertEquals(1, result.getCorrelations().size());
        Correlation correlation = result.getCorrelations().getFirst();
        assertEquals(CorrelationType.PRECEDES, correlation.getRelationshipType());
        assertEquals( Duration.ofSeconds(13), correlation.getTimeDifference());
    }
}

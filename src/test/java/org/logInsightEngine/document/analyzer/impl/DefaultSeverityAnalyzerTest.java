package org.logInsightEngine.document.analyzer.impl;

import org.junit.jupiter.api.Test;
import org.logInsightEngine.dtos.result.SeverityStatistics;
import org.logInsightEngine.model.domain.LogEntry;
import org.logInsightEngine.model.domain.LogLevel;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DefaultSeverityAnalyzerTest {
    private final DefaultSeverityAnalyzer severityAnalyzer = new DefaultSeverityAnalyzer();
    @Test
    public void testMixedLogLevels() {
        // Implement test logic for mixed log levels
        List<LogEntry> logEntries = new ArrayList<>();
        logEntries.add(new LogEntry(Instant.now(), LogLevel.INFO, "com.org.extractor", "main", "First log entry for info", null));
        logEntries.add(new LogEntry(Instant.now(), LogLevel.ERROR, "com.org.extractor", "main", "First log entry for error", null));
        logEntries.add(new LogEntry(Instant.now(), LogLevel.WARN, "com.org.extractor", "main", "First log entry for warn", null));
        logEntries.add(new LogEntry(Instant.now(), LogLevel.INFO, "com.org.extractor", "main", "Second log entry for info", null));
        logEntries.add(new LogEntry(Instant.now(), LogLevel.DEBUG, "com.org.extractor", "main", "First log entry for debug", null));

        SeverityStatistics severityStatistics =  severityAnalyzer.analyze(logEntries);
        assertEquals(5, severityStatistics.getCountByLevel().values().stream().mapToLong(Long::longValue).sum());
        assertEquals(2, severityStatistics.getCountByLevel().get(LogLevel.INFO));
        assertEquals(1, severityStatistics.getCountByLevel().get(LogLevel.ERROR));
        assertEquals(1, severityStatistics.getCountByLevel().get(LogLevel.WARN));
        assertEquals(1, severityStatistics.getCountByLevel().get(LogLevel.DEBUG));
    }

    @Test
    public void testSingleLogLevel() {
        // Implement test logic for single log level
        List<LogEntry> logEntries = new ArrayList<>();
        logEntries.add(new LogEntry(Instant.now(), LogLevel.INFO, "com.org.extractor", "main", "First log entry for info", null));
        logEntries.add(new LogEntry(Instant.now(), LogLevel.INFO, "com.org.extractor", "main", "Second log entry for info", null));

        SeverityStatistics severityStatistics =  severityAnalyzer.analyze(logEntries);
        assertEquals(2, severityStatistics.getCountByLevel().values().stream().mapToLong(Long::longValue).sum());
        assertEquals(2, severityStatistics.getCountByLevel().get(LogLevel.INFO));
    }

    @Test
    public void shouldTreatNullLogLevelAsUnknown() {
        // Implement test logic for entries with no log level
        List<LogEntry> logEntries = new ArrayList<>();
        logEntries.add(new LogEntry(Instant.now(), null, "com.org.extractor", "main", "First log entry with no level", null));
        logEntries.add(new LogEntry(Instant.now(), null, "com.org.extractor", "main", "Second log entry with no level", null));

        SeverityStatistics severityStatistics =  severityAnalyzer.analyze(logEntries);
        assertEquals(2, severityStatistics.getCountByLevel().values().stream().mapToLong(Long::longValue).sum());
        assertEquals(2, severityStatistics.getCountByLevel().get(LogLevel.UNKNOWN));
    }
    @Test
    public void shouldHandleEmptyLogEntries() {
        SeverityStatistics result =
                severityAnalyzer.analyze(List.of());

        for (LogLevel level : LogLevel.values()) {
            assertEquals(0L, result.getCountByLevel().get(level));
        }
    }
    @Test
    public void shouldRejectNullLogEntries() {
        // Implement test logic for no entries
        NullPointerException exception = assertThrows(NullPointerException.class,() -> severityAnalyzer.analyze(null));
        assertEquals( "Log entries cannot be null", exception.getMessage());
    }
}

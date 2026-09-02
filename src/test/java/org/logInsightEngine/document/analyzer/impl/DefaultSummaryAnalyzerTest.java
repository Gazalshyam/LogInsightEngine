package org.logInsightEngine.document.analyzer.impl;

import org.junit.jupiter.api.Test;
import org.logInsightEngine.dtos.result.Summary;
import org.logInsightEngine.model.domain.LogEntry;
import org.logInsightEngine.model.domain.LogLevel;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultSummaryAnalyzerTest {
    private final DefaultSummaryAnalyzer summaryAnalyzer = new DefaultSummaryAnalyzer();
    @Test
    public void analyzeEntriesWithTimestamps() {
        List<LogEntry> logEntries = new ArrayList<>();
        Instant timestamp1 = Instant.parse("2026-09-01T10:00:00Z");
        Instant timestamp2 = Instant.parse("2026-09-01T10:05:00Z");
        Instant timestamp3 = Instant.parse("2026-09-01T10:10:00Z");
        Instant timestamp4 = Instant.parse("2026-09-01T10:15:00Z");
        logEntries.add(new LogEntry(timestamp1, LogLevel.INFO, "com.org.extractor", "main", "First log entry", null));
        logEntries.add(new LogEntry(timestamp2, LogLevel.INFO, "com.org.extractor", "main", "First log entry", null));
        logEntries.add(new LogEntry(timestamp3, LogLevel.INFO, "com.org.extractor", "main", "First log entry", null));
        logEntries.add(new LogEntry(timestamp4, LogLevel.INFO, "com.org.extractor", "main", "First log entry", null));

        Summary summary  = summaryAnalyzer.analyze(logEntries);
        assertEquals(4, summary.getTotalLogEntries());
        assertNotNull(summary.getFirstTimestamp());
        assertNotNull(summary.getLastTimestamp());
        assertEquals(timestamp1, summary.getFirstTimestamp());
        assertEquals(timestamp4, summary.getLastTimestamp());
        assertNotNull(summary.getDuration());
        assertEquals(Duration.between(timestamp1, timestamp4), summary.getDuration());
    }

    @Test
    public void handleEntriesWithoutTimestamps() {
        List<LogEntry> logEntries = new ArrayList<>();
        Instant timestamp1 = Instant.parse("2026-09-01T10:00:00Z");
        Instant timestamp2 = Instant.parse("2026-09-01T10:05:00Z");
        Instant timestamp3 = Instant.parse("2026-09-01T10:10:00Z");
        logEntries.add(new LogEntry(null, LogLevel.INFO, "com.org.extractor", "main", "First log entry", null));
        logEntries.add(new LogEntry(timestamp1, LogLevel.INFO, "com.org.extractor", "main", "First log entry", null));
        logEntries.add(new LogEntry(timestamp2, LogLevel.INFO, "com.org.extractor", "main", "First log entry", null));
        logEntries.add(new LogEntry(timestamp3, LogLevel.INFO, "com.org.extractor", "main", "First log entry", null));

        Summary summary  = summaryAnalyzer.analyze(logEntries);
        assertEquals(4, summary.getTotalLogEntries());
        assertNotNull(summary.getFirstTimestamp());
        assertNotNull(summary.getLastTimestamp());
        assertEquals(timestamp1, summary.getFirstTimestamp());
        assertEquals(timestamp3, summary.getLastTimestamp());
        assertNotNull(summary.getDuration());
        assertEquals(Duration.between(timestamp1, timestamp3), summary.getDuration());
    }

    @Test
    public void handleEntriesOutOfChronologicalOrder() {
        List<LogEntry> logEntries = new ArrayList<>();
        Instant timestamp1 = Instant.parse("2026-09-01T10:00:00Z");
        Instant timestamp2 = Instant.parse("2026-09-01T10:05:00Z");
        Instant timestamp3 = Instant.parse("2026-09-01T10:10:00Z");
        Instant timestamp4 = Instant.parse("2026-09-01T10:15:00Z");
        logEntries.add(new LogEntry(timestamp3, LogLevel.INFO, "com.org.extractor", "main", "First log entry", null));
        logEntries.add(new LogEntry(timestamp1, LogLevel.INFO, "com.org.extractor", "main", "First log entry", null));
        logEntries.add(new LogEntry(timestamp2, LogLevel.INFO, "com.org.extractor", "main", "First log entry", null));
        logEntries.add(new LogEntry(timestamp4, LogLevel.INFO, "com.org.extractor", "main", "First log entry", null));

        Summary summary  = summaryAnalyzer.analyze(logEntries);
        assertEquals(4, summary.getTotalLogEntries());
        assertNotNull(summary.getFirstTimestamp());
        assertNotNull(summary.getLastTimestamp());
        assertEquals(timestamp1, summary.getFirstTimestamp());
        assertEquals(timestamp4, summary.getLastTimestamp());
        assertNotNull(summary.getDuration());
        assertEquals(Duration.between(timestamp1, timestamp4), summary.getDuration());
    }

    @Test
    public void handleIgnoreNullTimestampsForTimeRange() {
        List<LogEntry> logEntries = new ArrayList<>();
        Instant timestamp1 = Instant.parse("2026-09-01T10:00:00Z");
        Instant timestamp4 = Instant.parse("2026-09-01T10:15:00Z");
        logEntries.add(new LogEntry(null, LogLevel.INFO, "com.org.extractor", "main", "First log entry", null));
        logEntries.add(new LogEntry(timestamp1, LogLevel.INFO, "com.org.extractor", "main", "First log entry", null));
        logEntries.add(new LogEntry(null, LogLevel.INFO, "com.org.extractor", "main", "First log entry", null));
        logEntries.add(new LogEntry(timestamp4, LogLevel.INFO, "com.org.extractor", "main", "First log entry", null));

        Summary summary  = summaryAnalyzer.analyze(logEntries);
        assertEquals(4, summary.getTotalLogEntries());
        assertNotNull(summary.getFirstTimestamp());
        assertNotNull(summary.getLastTimestamp());
        assertEquals(timestamp1, summary.getFirstTimestamp());
        assertEquals(timestamp4, summary.getLastTimestamp());
        assertNotNull(summary.getDuration());
        assertEquals(Duration.between(timestamp1, timestamp4), summary.getDuration());
    }

    @Test
    public void shouldHandleEntriesWithOnlyNullTimestamps(){
        List<LogEntry> logEntries = new ArrayList<>();
        logEntries.add(new LogEntry(null, LogLevel.INFO, "com.org.extractor", "main", "First log entry", null));
        logEntries.add(new LogEntry(null, LogLevel.INFO, "com.org.extractor", "main", "First log entry", null));
        logEntries.add(new LogEntry(null, LogLevel.INFO, "com.org.extractor", "main", "First log entry", null));
        logEntries.add(new LogEntry(null, LogLevel.INFO, "com.org.extractor", "main", "First log entry", null));

        Summary summary  = summaryAnalyzer.analyze(logEntries);
        assertEquals(4, summary.getTotalLogEntries());
        assertNull(summary.getFirstTimestamp());
        assertNull(summary.getLastTimestamp());
        assertEquals(Duration.ZERO,summary.getDuration());
    }

    @Test
    public void shouldHandleEmptyLogEntries() {
        List<LogEntry> logEntries = new ArrayList<>();
        Summary summary  = summaryAnalyzer.analyze(logEntries);
        assertEquals(0, summary.getTotalLogEntries());
        assertNull(summary.getFirstTimestamp());
        assertNull(summary.getLastTimestamp());
        assertEquals(Duration.ZERO,summary.getDuration());
    }
    @Test
    public void shouldRejectNullLogEntries() {
        NullPointerException exception = assertThrows(NullPointerException.class,() -> summaryAnalyzer.analyze(null));
        assertEquals( "Log entries cannot be null", exception.getMessage());
    }
}

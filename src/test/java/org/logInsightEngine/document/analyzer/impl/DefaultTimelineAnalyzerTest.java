package org.logInsightEngine.document.analyzer.impl;

import org.junit.jupiter.api.Test;
import org.logInsightEngine.dtos.result.*;
import org.logInsightEngine.model.domain.LogEntry;
import org.logInsightEngine.model.domain.LogLevel;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DefaultTimelineAnalyzerTest {
    private final DefaultTimelineAnalyzer defaultTimelineAnalyzer = new DefaultTimelineAnalyzer();

    private LogEntry createLogEntry(LogLevel level, String logger, String message, String thread, Instant timestamp) {
        LogEntry logEntry = new LogEntry();
        logEntry.setLevel(level);
        logEntry.setLogger(logger);
        logEntry.setMessage(message);
        logEntry.setThread(thread);
        logEntry.setTimestamp(timestamp);
        return logEntry;
    }

    private Finding createFinding(String id, FindingCategory category, String description, Instant firstOccurrence, Instant lastOccurrence, long occurrenceCount, PriorityLevel level, String title, List<String> errorGroupIds, Set<String> threads, Set<String> loggers) {
        Finding finding = new Finding();
        finding.setId(id);
        finding.setCategory(category);
        finding.setDescription(description);
        finding.setFirstOccurrence(firstOccurrence);
        finding.setLastOccurrence(lastOccurrence);
        finding.setOccurrenceCount(occurrenceCount);
        finding.setPriorityLevel(level);
        finding.setTitle(title);
        finding.setRelatedErrorGroupIds(errorGroupIds);
        Impact impact = new Impact();
        impact.setThreads(threads);
        impact.setLoggers(loggers);
        finding.setImpact(impact);
        return finding;
    }

    @Test
    public void testNullFindings() {
        List<LogEntry> logEntries = new ArrayList<>();
        LogEntry logEntry = createLogEntry(LogLevel.INFO, "Information", "Application found", "main", Instant.ofEpochSecond(1234567890L));
        assertThrows(NullPointerException.class, () -> defaultTimelineAnalyzer.analyze(List.of(logEntry), null));
    }

    @Test
    public void testNullLogEntries() {
        Finding finding = createFinding("id1", FindingCategory.ERROR, "Hi", Instant.ofEpochSecond(1234567890L), Instant.ofEpochSecond(1234567890L), 1, PriorityLevel.CRITICAL, "Critical issue", List.of("id10"), Set.of("main"), Set.of("org.service"));
        assertThrows(NullPointerException.class, () -> defaultTimelineAnalyzer.analyze(null, List.of(finding)));
    }

    @Test
    public void testNullEntries() {
        assertThrows(NullPointerException.class, () -> defaultTimelineAnalyzer.analyze(null, null));
    }

    @Test
    public void testEmptyEntries() {
        Timeline timeline = defaultTimelineAnalyzer.analyze(List.of(), List.of());
        assertEquals(0, timeline.getEvents().size());
    }

    @Test
    public void testNullTimestamp() {
        LogEntry logEntry = createLogEntry(LogLevel.INFO, "Information", "Application found", "main", null);

        Timeline timeline = defaultTimelineAnalyzer.analyze(List.of(logEntry), List.of());
        assertEquals(0, timeline.getEvents().size());
    }

    @Test //add more assertions
    public void testInfoLogs() {
        LogEntry logEntry = createLogEntry(LogLevel.INFO, "Information", "Application found", "main", Instant.ofEpochSecond(1234567890L));
        Timeline timeline = defaultTimelineAnalyzer.analyze(List.of(logEntry), List.of());
        TimelineEvent timelineEvent = timeline.getEvents().getFirst();
        assertEquals(1, timeline.getEvents().size());
        assertEquals(LogLevel.INFO, timelineEvent.getLevel());
        assertEquals(Instant.ofEpochSecond(1234567890L), timelineEvent.getTimestamp());
        assertEquals("Application found", timelineEvent.getDescription());
        assertEquals("Application found", timelineEvent.getTitle());
    }

    @Test//add more assertions
    public void testWarnLogs() {
        LogEntry logEntry = createLogEntry(LogLevel.WARN, "Information", "Application found", "main", Instant.ofEpochSecond(1234567890L));
        Timeline timeline = defaultTimelineAnalyzer.analyze(List.of(logEntry), List.of());
        TimelineEvent timelineEvent = timeline.getEvents().getFirst();
        assertEquals(1, timeline.getEvents().size());
        assertEquals(LogLevel.WARN, timelineEvent.getLevel());
        assertEquals(Instant.ofEpochSecond(1234567890L), timelineEvent.getTimestamp());
        assertEquals("Application found", timelineEvent.getDescription());
        assertEquals("Application found", timelineEvent.getTitle());
    }

    @Test//add findings, convert info to error log
    public void testErrorLogIsExcludedAndFindingIsIncluded() {
        LogEntry logEntry = createLogEntry(LogLevel.ERROR, "Information", "Application found", "main", Instant.ofEpochSecond(1234567890L));
        Finding finding = createFinding("id1", FindingCategory.ERROR, "Information|ApplicationNotFound", Instant.ofEpochSecond(1234567890L), Instant.ofEpochSecond(1234567980L), 2, PriorityLevel.HIGH, "Title", List.of("id1", "id2"), Set.of("service", "controller"), Set.of("Main", "Secondary"));
        Timeline timeline = defaultTimelineAnalyzer.analyze(List.of(logEntry), List.of(finding));
        TimelineEvent timelineEvent = timeline.getEvents().getFirst();
        assertEquals(1, timeline.getEvents().size());
        assertEquals(LogLevel.ERROR, timelineEvent.getLevel());
        assertEquals("id1", timelineEvent.getFindingId());
        assertEquals(Instant.ofEpochSecond(1234567890L), timelineEvent.getTimestamp());
        assertEquals("Information|ApplicationNotFound", timelineEvent.getDescription());
        assertEquals("Title", timelineEvent.getTitle());
    }

    @Test//make sure debug logs are excluded
    public void testDebugLogs() {
        LogEntry logEntry = createLogEntry(LogLevel.DEBUG, "Information", "Application found", "main", Instant.ofEpochSecond(1234567890L));
        Timeline timeline = defaultTimelineAnalyzer.analyze(List.of(logEntry), List.of());
        assertEquals(0, timeline.getEvents().size());
    }

    @Test//make sure debug/trace/unkown logs are excluded
    public void testUnknownLogs() {
        LogEntry logEntry = createLogEntry(LogLevel.UNKNOWN, "Information", "Application found", "main", Instant.ofEpochSecond(1234567890L));
        Timeline timeline = defaultTimelineAnalyzer.analyze(List.of(logEntry), List.of());
        assertEquals(0, timeline.getEvents().size());
    }

    @Test//make sure all logs are excluded
    public void testAllLogs() {
        LogEntry logEntry = createLogEntry(LogLevel.ALL, "Information", "Application found", "main", Instant.ofEpochSecond(1234567890L));
        Timeline timeline = defaultTimelineAnalyzer.analyze(List.of(logEntry), List.of());
        assertEquals(0, timeline.getEvents().size());
    }

    @Test//make sure trace logs are excluded
    public void testTraceLogs() {
        LogEntry logEntry = createLogEntry(LogLevel.TRACE, "Information", "Application found", "main", Instant.ofEpochSecond(1234567890L));
        Timeline timeline = defaultTimelineAnalyzer.analyze(List.of(logEntry), List.of());
        assertEquals(0, timeline.getEvents().size());
    }


    @Test//test Findings of error logs and see whether the values are being correctly mapped
    public void testFindings() {
        Finding finding = createFinding("id1", FindingCategory.ERROR, "Information|ApplicationNotFound", Instant.ofEpochSecond(1234567890L), Instant.ofEpochSecond(1234567980L), 2, PriorityLevel.HIGH, "Title", List.of("id1", "id2"), Set.of("service", "controller"), Set.of("Main", "Secondary"));
        Timeline timeline = defaultTimelineAnalyzer.analyze(List.of(), List.of(finding));
        TimelineEvent timelineEvent = timeline.getEvents().getFirst();
        assertEquals(1, timeline.getEvents().size());
        assertEquals(LogLevel.ERROR, timelineEvent.getLevel());
        assertEquals("id1", timelineEvent.getFindingId());
        assertEquals(Instant.ofEpochSecond(1234567890L), timelineEvent.getTimestamp());
        assertEquals("Information|ApplicationNotFound", timelineEvent.getDescription());
        assertEquals("Title", timelineEvent.getTitle());
    }

}

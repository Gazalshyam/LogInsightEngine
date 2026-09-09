package org.logInsightEngine.document.analyzer.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.logInsightEngine.dtos.result.ErrorAnalysisResult;
import org.logInsightEngine.dtos.result.ErrorGroup;
import org.logInsightEngine.model.domain.LogEntry;
import org.logInsightEngine.model.domain.LogLevel;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DefaultErrorAnalyzerImplementationTest {
    private final DefaultErrorAnalyzerImplementation defaultErrorAnalyzerImplementation = new DefaultErrorAnalyzerImplementation();

    @Test
    public void testAnalyzeWithNullLogEntries() {
        assertThrows(NullPointerException.class, () -> defaultErrorAnalyzerImplementation.analyze(null));
    }

    @Test
    public void testAnalyzeWithEmptyLogEntries() {
        ErrorAnalysisResult result = defaultErrorAnalyzerImplementation.analyze(List.of());
        assertEquals(0, result.getErrorGroups().size());
    }

    @Test
    public void testAnalyzeWithValidLogEntries() {
        // You can add a test case with valid log entries here
        // For example, create a list of LogEntry objects and call the analyze method
        // Then assert the expected results
        Instant timestamp1 = Instant.parse("2026-09-01T10:00:00Z");
        Instant timestamp2 = Instant.parse("2026-09-01T10:15:00Z");
        List<LogEntry> logEntries = List.of(
                new LogEntry(timestamp1, LogLevel.ERROR, "com.example.MyClass", "Thread-1", "An error occurred", "java.lang.NullPointerException: Something went wrong\n\tat com.example.MyClass.method(MyClass.java:10)"),
                new LogEntry(timestamp2, LogLevel.ERROR, "com.example.MyClass", "Thread-2", "An error occurred", "java.lang.NullPointerException: Something went wrong\n\tat com.example.MyClass.method(MyClass.java:10)")
        );
        ErrorAnalysisResult errorAnalysisResult = defaultErrorAnalyzerImplementation.analyze(logEntries);
        List<ErrorGroup> errorGroups = errorAnalysisResult.getErrorGroups();
        assertEquals(1, errorGroups.size());
        assertEquals("An error occurred", errorGroups.getFirst().getMessage());
        assertEquals("java.lang.NullPointerException", errorGroups.getFirst().getExceptionType());
        assertEquals(2, errorGroups.getFirst().getOccurrenceCount());
        assertEquals(timestamp1, errorGroups.getFirst().getFirstOccurrence());
        assertEquals(timestamp2, errorGroups.getFirst().getLastOccurrence());
        assertEquals(Set.of("Thread-2", "Thread-1"), errorGroups.getFirst().getImpact().getThreads());
        assertEquals(Set.of("com.example.MyClass"), errorGroups.getFirst().getImpact().getLoggers());
    }

    @Test
    public void testAnalyzeWithLogEntriesHavingDifferentExceptions() {
        Instant timestamp1 = Instant.parse("2026-09-01T10:00:00Z");
        Instant timestamp2 = Instant.parse("2026-09-01T10:15:00Z");
        List<LogEntry> logEntries = List.of(
                new LogEntry(timestamp1, LogLevel.ERROR, "com.example.MyClass", "Thread-1", "An error occurred", "java.lang.NullPointerException: Something went wrong\n\tat com.example.MyClass.method(MyClass.java:10)"),
                new LogEntry(timestamp2, LogLevel.ERROR, "com.example.MyClass", "Thread-2", "Another error occurred", "java.lang.IllegalArgumentException: Invalid argument\n\tat com.example.MyClass.method(MyClass.java:20)")
        );
        ErrorAnalysisResult errorAnalysisResult = defaultErrorAnalyzerImplementation.analyze(logEntries);
        List<ErrorGroup> errorGroups = errorAnalysisResult.getErrorGroups();
        assertEquals(2, errorGroups.size());
        assertEquals("An error occurred", errorGroups.getFirst().getMessage());
        assertEquals("java.lang.NullPointerException", errorGroups.getFirst().getExceptionType());
        assertEquals(1, errorGroups.getFirst().getOccurrenceCount());
        assertEquals(timestamp1, errorGroups.getFirst().getFirstOccurrence());
        assertEquals(timestamp1, errorGroups.getFirst().getLastOccurrence());
        assertEquals("Another error occurred", errorGroups.getLast().getMessage());
        assertEquals("java.lang.IllegalArgumentException", errorGroups.getLast().getExceptionType());
        assertEquals(1, errorGroups.getLast().getOccurrenceCount());
        assertEquals(timestamp2, errorGroups.getLast().getFirstOccurrence());
        assertEquals(timestamp2, errorGroups.getLast().getLastOccurrence());
        assertEquals(Set.of("Thread-2"), errorGroups.getLast().getImpact().getThreads());
        assertEquals(Set.of("Thread-1"), errorGroups.getFirst().getImpact().getThreads());
        assertEquals(Set.of("com.example.MyClass"), errorGroups.getFirst().getImpact().getLoggers());
        assertEquals(Set.of("com.example.MyClass"), errorGroups.getLast().getImpact().getLoggers());


    }

    @Test
    public void testAnalyzeWithLogEntriesWithSameExceptionButDifferentMessages() {
        Instant timestamp1 = Instant.parse("2026-09-01T10:00:00Z");
        Instant timestamp2 = Instant.parse("2026-09-01T10:15:00Z");
        List<LogEntry> logEntries = List.of(
                new LogEntry(timestamp1, LogLevel.ERROR, "com.example.MyClass", "Thread-1", "An error occurred", "java.lang.NullPointerException: Something went wrong\n\tat com.example.MyClass.method(MyClass.java:10)"),
                new LogEntry(timestamp2, LogLevel.ERROR, "com.example.MyClass", "Thread-2", "A different error occurred", "java.lang.NullPointerException: Something went wrong\n\tat com.example.MyClass.method(MyClass.java:10)")
        );
        ErrorAnalysisResult errorAnalysisResult = defaultErrorAnalyzerImplementation.analyze(logEntries);
        List<ErrorGroup> errorGroups = errorAnalysisResult.getErrorGroups();
        assertEquals(2, errorGroups.size());
        assertEquals("An error occurred", errorGroups.getFirst().getMessage());
        assertEquals("java.lang.NullPointerException", errorGroups.getFirst().getExceptionType());
        assertEquals(1, errorGroups.getFirst().getOccurrenceCount());
        assertEquals(timestamp1, errorGroups.getFirst().getFirstOccurrence());
        assertEquals(timestamp1, errorGroups.getFirst().getLastOccurrence());
        assertEquals("A different error occurred", errorGroups.getLast().getMessage());
        assertEquals("java.lang.NullPointerException", errorGroups.getLast().getExceptionType());
        assertEquals(1, errorGroups.getLast().getOccurrenceCount());
        assertEquals(timestamp2, errorGroups.getLast().getFirstOccurrence());
        assertEquals(timestamp2, errorGroups.getLast().getLastOccurrence());
        assertEquals(Set.of("Thread-2"), errorGroups.getLast().getImpact().getThreads());
        assertEquals(Set.of("Thread-1"), errorGroups.getFirst().getImpact().getThreads());
        assertEquals(Set.of("com.example.MyClass"), errorGroups.getFirst().getImpact().getLoggers());
        assertEquals(Set.of("com.example.MyClass"), errorGroups.getLast().getImpact().getLoggers());

    }

    @Test
    public void testAnalyzeWithDifferentExceptionAndSameMessage() {
        Instant timestamp1 = Instant.parse("2026-09-01T10:00:00Z");
        Instant timestamp2 = Instant.parse("2026-09-01T10:15:00Z");
        List<LogEntry> logEntries = List.of(
                new LogEntry(timestamp1, LogLevel.ERROR, "com.example.MyClass", "Thread-1", "An error occurred", "java.lang.NullPointerException: Something went wrong\n\tat com.example.MyClass.method(MyClass.java:10)"),
                new LogEntry(timestamp2, LogLevel.ERROR, "com.example.MyClass", "Thread-2", "An error occurred", "java.lang.IllegalArgumentException: Invalid argument\n\tat com.example.MyClass.method(MyClass.java:20)")
        );
        ErrorAnalysisResult errorAnalysisResult = defaultErrorAnalyzerImplementation.analyze(logEntries);
        List<ErrorGroup> errorGroups = errorAnalysisResult.getErrorGroups();
        assertEquals(2, errorGroups.size());
        assertEquals("An error occurred", errorGroups.getFirst().getMessage());
        assertEquals("java.lang.NullPointerException", errorGroups.getFirst().getExceptionType());
        assertEquals(1, errorGroups.getFirst().getOccurrenceCount());
        assertEquals(timestamp1, errorGroups.getFirst().getFirstOccurrence());
        assertEquals(timestamp1, errorGroups.getFirst().getLastOccurrence());
        assertEquals("An error occurred", errorGroups.getLast().getMessage());
        assertEquals("java.lang.IllegalArgumentException", errorGroups.getLast().getExceptionType());
        assertEquals(1, errorGroups.getLast().getOccurrenceCount());
        assertEquals(timestamp2, errorGroups.getLast().getFirstOccurrence());
        assertEquals(timestamp2, errorGroups.getLast().getLastOccurrence());
        assertEquals(Set.of("Thread-2"), errorGroups.getLast().getImpact().getThreads());
        assertEquals(Set.of("Thread-1"), errorGroups.getFirst().getImpact().getThreads());
        assertEquals(Set.of("com.example.MyClass"), errorGroups.getFirst().getImpact().getLoggers());
        assertEquals(Set.of("com.example.MyClass"), errorGroups.getLast().getImpact().getLoggers());

    }

    @Test
    public void testAnalyzeWithDifferentLogLevels() {
        Instant timestamp1 = Instant.parse("2026-09-01T10:00:00Z");
        Instant timestamp2 = Instant.parse("2026-09-01T10:15:00Z");
        List<LogEntry> logEntries = List.of(
                new LogEntry(timestamp1, LogLevel.WARN, "com.example.MyClass", "Thread-1", "A warning occurred", "java.lang.NullPointerException: Something went wrong\n\tat com.example.MyClass.method(MyClass.java:10)"),
                new LogEntry(timestamp2, LogLevel.ERROR, "com.example.MyClass", "Thread-2", "An error occurred", "java.lang.IllegalArgumentException: Invalid argument\n\tat com.example.MyClass.method(MyClass.java:20)")
        );
        ErrorAnalysisResult errorAnalysisResult = defaultErrorAnalyzerImplementation.analyze(logEntries);
        List<ErrorGroup> errorGroups = errorAnalysisResult.getErrorGroups();
        assertEquals(1, errorGroups.size());
        assertEquals("An error occurred", errorGroups.getFirst().getMessage());
        assertEquals("java.lang.IllegalArgumentException", errorGroups.getFirst().getExceptionType());
        assertEquals(1, errorGroups.getFirst().getOccurrenceCount());
        assertEquals(timestamp2, errorGroups.getFirst().getFirstOccurrence());
        assertEquals(timestamp2, errorGroups.getFirst().getLastOccurrence());
        assertEquals(Set.of("com.example.MyClass"), errorGroups.getFirst().getImpact().getLoggers());
        assertEquals(Set.of("Thread-2"), errorGroups.getFirst().getImpact().getThreads());
    }

    @Test
    public void testAnalyzeWithOutOfOrderLogEntries() {
        // You can add a test case with valid log entries here
        // For example, create a list of LogEntry objects and call the analyze method
        // Then assert the expected results

        Instant timestamp1 = Instant.parse("2026-09-01T10:00:00Z");
        Instant timestamp2 = Instant.parse("2026-09-01T10:15:00Z");
        List<LogEntry> logEntries = List.of(
                new LogEntry(timestamp2, LogLevel.ERROR, "com.example.MyClass", "Thread-2", "An error occurred", "java.lang.NullPointerException: Something went wrong\n\tat com.example.MyClass.method(MyClass.java:10)"),
                new LogEntry(timestamp1, LogLevel.ERROR, "com.example.MyClass", "Thread-1", "An error occurred", "java.lang.NullPointerException: Something went wrong\n\tat com.example.MyClass.method(MyClass.java:10)")
        );
        ErrorAnalysisResult errorAnalysisResult = defaultErrorAnalyzerImplementation.analyze(logEntries);
        List<ErrorGroup> errorGroups = errorAnalysisResult.getErrorGroups();
        assertEquals(1, errorGroups.size());
        assertEquals("An error occurred", errorGroups.getFirst().getMessage());
        assertEquals("java.lang.NullPointerException", errorGroups.getFirst().getExceptionType());
        assertEquals(2, errorGroups.getFirst().getOccurrenceCount());
        assertEquals(timestamp1, errorGroups.getFirst().getFirstOccurrence());
        assertEquals(timestamp2, errorGroups.getFirst().getLastOccurrence());
        assertEquals(Set.of("Thread-2", "Thread-1"), errorGroups.getFirst().getImpact().getThreads());
        assertEquals(Set.of("com.example.MyClass"), errorGroups.getFirst().getImpact().getLoggers());
    }

    @Test
    public void testAnalyzeWithNullTimestampLogEntries() {
        // You can add a test case with valid log entries here
        // For example, create a list of LogEntry objects and call the analyze method
        // Then assert the expected results

        Instant timestamp1 = Instant.parse("2026-09-01T10:00:00Z");
        Instant timestamp2 = Instant.parse("2026-09-01T10:15:00Z");
        List<LogEntry> logEntries = List.of(
                new LogEntry(null, LogLevel.ERROR, "com.example.MyClass", "Thread-3", "An error occurred", "java.lang.NullPointerException: Something went wrong\n\tat com.example.MyClass.method(MyClass.java:10)"),
                new LogEntry(timestamp2, LogLevel.ERROR, "com.example.MyClass", "Thread-2", "An error occurred", "java.lang.NullPointerException: Something went wrong\n\tat com.example.MyClass.method(MyClass.java:10)"),
                new LogEntry(timestamp1, LogLevel.ERROR, "com.example.MyClass", "Thread-1", "An error occurred", "java.lang.NullPointerException: Something went wrong\n\tat com.example.MyClass.method(MyClass.java:10)")
        );
        ErrorAnalysisResult errorAnalysisResult = defaultErrorAnalyzerImplementation.analyze(logEntries);
        List<ErrorGroup> errorGroups = errorAnalysisResult.getErrorGroups();
        assertEquals(1, errorGroups.size());
        assertEquals("An error occurred", errorGroups.getFirst().getMessage());
        assertEquals("java.lang.NullPointerException", errorGroups.getFirst().getExceptionType());
        assertEquals(3, errorGroups.getFirst().getOccurrenceCount());
        assertEquals(timestamp1, errorGroups.getFirst().getFirstOccurrence());
        assertEquals(timestamp2, errorGroups.getFirst().getLastOccurrence());
        assertEquals(Set.of("Thread-3", "Thread-2", "Thread-1"), errorGroups.getFirst().getImpact().getThreads());
        assertEquals(Set.of("com.example.MyClass"), errorGroups.getFirst().getImpact().getLoggers());
    }

    @Test
    public void testAnalyzeWithNullLogEntryInList() {
        Instant timestamp1 = Instant.parse("2026-09-01T10:00:00Z");

        assertThrows(NullPointerException.class, () -> defaultErrorAnalyzerImplementation.analyze(List.of(
                new LogEntry(timestamp1, LogLevel.ERROR, "com.example.MyClass", "Thread-1", "An error occurred", "java.lang.NullPointerException: Something went wrong\n\tat com.example.MyClass.method(MyClass.java:10)"),
                null
        )));
    }

}

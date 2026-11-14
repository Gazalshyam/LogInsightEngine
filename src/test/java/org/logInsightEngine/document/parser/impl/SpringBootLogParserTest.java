package org.logInsightEngine.document.parser.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.logInsightEngine.TestUtils;
import org.logInsightEngine.model.domain.ExtractedDocument;
import org.logInsightEngine.model.domain.LogEntry;
import org.logInsightEngine.model.domain.LogLevel;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class SpringBootLogParserTest {
    private final SpringBootLogParser parser = new SpringBootLogParser();

    @Test
    public void shouldParseValidInfoLogEntry() throws IOException {
        //arrange
        ExtractedDocument document = TestUtils.loadDocument("logs/valid-info.log");
        //act
        List<LogEntry> entries = parser.parse(document);
        //assert
        assertEquals(1, entries.size());

        testInfoLog(entries, 0);

    }

    private void testInfoLog(List<LogEntry> entries, int logPositon) {

        LogEntry entry = entries.get(logPositon);

        assertEquals(LogLevel.INFO, entry.getLevel());
        assertEquals("main", entry.getThread());
        assertEquals("org.logInsightEngine.service.AnalysisService", entry.getLogger());
        assertEquals("Analysis started", entry.getMessage());
        assertNotNull(entry.getTimestamp());
    }

    @Test
    public void shouldParseWarnLog() throws IOException {
        //arrange the input
        ExtractedDocument document = TestUtils.loadDocument("logs/valid-warn.log");
        //act
        List<LogEntry> entries = parser.parse(document);
        //assert
        assertEquals(1, entries.size());
        testWarnLog(entries, 0);
    }

    private void testWarnLog(List<LogEntry> entries, int logPosition) {
        LogEntry entry = entries.get(logPosition);
        assertEquals(LogLevel.WARN, entry.getLevel()); //check the level with what is returned from the parser
        assertEquals("main", entry.getThread()); //check the thread with what is returned from the parser
        assertEquals("org.logInsightEngine.service.DocumentService", entry.getLogger());
        assertEquals("Unsupported file extension '.csv'. Falling back to plain text extraction.", entry.getMessage());
        assertNotNull(entry.getTimestamp());
    }

    @Test
    public void shouldParseErrorLog() throws IOException {
        //arrange
        ExtractedDocument document = TestUtils.loadDocument("logs/valid-error.log");
        //act
        List<LogEntry> entries = parser.parse(document);
        //assert
        assertEquals(1, entries.size()); //check the size of the logs

        testErrorLog(entries, 0);

    }

    private void testErrorLog(List<LogEntry> entries, int logPosition) {
        LogEntry entry = entries.get(logPosition);
        assertEquals(LogLevel.ERROR, entry.getLevel());
        assertEquals("main", entry.getThread());
        assertEquals("org.logInsightEngine.document.parser.impl.SpringBootLogParser", entry.getLogger());
        assertEquals("Failed to parse log document", entry.getMessage());
        assertNotNull(entry.getTimestamp());
    }

    @Test
    public void shouldParseDebugLog() throws IOException {
        //arrange
        ExtractedDocument document = TestUtils.loadDocument("logs/valid-debug.log");
        //act
        List<LogEntry> entries = parser.parse(document);
        //assert
        assertEquals(1, entries.size());
        testDebugLog(entries, 0);
    }

    private void testDebugLog(List<LogEntry> entries, int logPosition) {
        LogEntry entry = entries.get(logPosition);
        assertEquals(LogLevel.DEBUG, entry.getLevel());
        assertEquals("main", entry.getThread());
        assertEquals("org.logInsightEngine.document.parser.impl.SpringBootLogParser", entry.getLogger());
        assertEquals("Parsing started for application.log", entry.getMessage());
        assertNotNull(entry.getTimestamp());
    }

    @Test
    public void shouldParseMultiLineStackTrace() throws IOException {
        ExtractedDocument document = TestUtils.loadDocument("logs/multiline/valid-stacktrace.log");
        List<LogEntry> entries = parser.parse(document);
        assertEquals(1, entries.size());
        testStackTrace(entries, 0);
    }

    private void testStackTrace(List<LogEntry> entries, int logPosition) {
        LogEntry entry = entries.get(logPosition);
        assertEquals(LogLevel.ERROR, entry.getLevel());
        assertEquals("main", entry.getThread());
        assertEquals("org.logInsightEngine.document.parser.impl.SpringBootLogParser", entry.getLogger());
        assertEquals("Failed to parse uploaded log file", entry.getMessage());
        assertFalse(entry.getMessage().contains("NullPointerException"));
        assertFalse(entry.getMessage().contains("at "));
        assertFalse(entry.getMessage().contains("Caused by"));
        assertNotNull(entry.getTimestamp());
        assertNotNull(entry.getStackTrace());
        assertFalse(entry.getStackTrace().contains("Failed to parse uploaded log file"));
        assertTrue(entry.getStackTrace().contains("java.lang.NullPointerException"));
        assertTrue(entry.getStackTrace().contains("IllegalArgumentException"));
        assertTrue(entry.getStackTrace().contains("Caused by"));
        assertTrue(entry.getStackTrace().contains("SpringBootLogParser.parse"));
        assertTrue(entry.getStackTrace().contains("LogAnalysisService.analyze"));
    }

    @Test
    public void shouldParseMultiLineLogs() throws IOException {
        ExtractedDocument document = TestUtils.loadDocument("logs/multiline/valid-multiline.log");
        List<LogEntry> entries = parser.parse(document);
        assertEquals(3, entries.size());
        testInfoLog(entries, 0);
        LogEntry entry = entries.get(1);
        assertEquals(LogLevel.ERROR, entry.getLevel());
        assertEquals("http-nio-8080-exec-8", entry.getThread());
        assertEquals("classOne", entry.getLogger());
        assertEquals("Index out of range\n" +
                "Processing uploaded document\n" +
                "Validating document metadata\n" +
                "Document validation completed successfully", entry.getMessage());
        assertNotNull(entry.getTimestamp());
        testErrorLog(entries, 2);
    }

    @Test
    public void shouldParseMultiLineLogsStackTrace() throws IOException {
        ExtractedDocument document = TestUtils.loadDocument("logs/multiline/valid-multiline-logs-stacktrace.log");
        List<LogEntry> entries = parser.parse(document);
        assertEquals(2, entries.size());
        testStackTrace(entries, 0);
        testErrorLog(entries, 1);
    }

    @Test
    public void shouldParseMultiLineBlankLogs() throws IOException {
        ExtractedDocument document = TestUtils.loadDocument("logs/multiline/valid-multiline-blank.log");
        List<LogEntry> entries = parser.parse(document);
        assertEquals(2, entries.size());
        testInfoLog(entries, 0);
        testErrorLog(entries, 1);
    }

    @Test
    public void shouldParseMultiLineLogsStackTraceBlank() throws IOException {
        ExtractedDocument document = TestUtils.loadDocument("logs/multiline/valid-multiline-logs-stacktrace-blank.log");
        List<LogEntry> entries = parser.parse(document);
        assertEquals(2, entries.size());
        testStackTrace(entries, 0);
        testErrorLog(entries, 1);
    }

    @Test
    public void shouldSkipBlankLogs() throws IOException {
        ExtractedDocument document = TestUtils.loadDocument("logs/multiline/valid-blank.log");
        List<LogEntry> entries = parser.parse(document);
        assertEquals(0, entries.size());
    }

    @Test
    public void shouldIgnoreMalformedLogHeaders() throws IOException {
        ExtractedDocument document = TestUtils.loadDocument("logs/multiline/malformed/invalid-multiline-logs.log");
        List<LogEntry> entries = parser.parse(document);
        assertEquals(3, entries.size());
        testInfoLog(entries, 0);
        testStackTrace(entries, 1);
        testErrorLog(entries, 2);

    }

    @Test
    public void shouldSkipUnsupportedLogLevel() throws IOException {
        ExtractedDocument document = TestUtils.loadDocument("logs/multiline/malformed/invalid-log-level.log");
        List<LogEntry> entries = parser.parse(document);
        assertTrue(entries.isEmpty());
    }

    @Test
    public void shouldSkipMissingThread() throws IOException {
        ExtractedDocument document = TestUtils.loadDocument("logs/multiline/malformed/invalid-thread.log");
        List<LogEntry> entries = parser.parse(document);
        assertEquals(1, entries.size());

        LogEntry entry = entries.get(0);

        assertEquals(LogLevel.ERROR, entry.getLevel());
        assertEquals("virtual", entry.getThread());
        assertEquals("org.logInsightEngine.document.parser.impl.SpringBootLogParser", entry.getLogger());
        assertEquals("Failed to parse log document", entry.getMessage());
        assertNotNull(entry.getTimestamp());

    }

    @Test
    public void shouldSkipMissingLogger() throws IOException {
        ExtractedDocument document = TestUtils.loadDocument("logs/multiline/malformed/invalid-logger.log");
        List<LogEntry> entries = parser.parse(document);
        assertEquals(0, entries.size());
    }

    @Test
    public void shouldSkipMissingMessageSeparator() throws IOException {
        ExtractedDocument document = TestUtils.loadDocument("logs/multiline/malformed/invalid-message.log");
        List<LogEntry> entries = parser.parse(document);
        assertEquals(0, entries.size());
    }

    @Test
    public void shouldSkipInvalidTimestamp() throws IOException {
        ExtractedDocument document = TestUtils.loadDocument("logs/timestamp/invalid-iso-format.log");
        List<LogEntry> entries = parser.parse(document);
        assertEquals(0, entries.size());
    }

    @Test
    public void shouldProcessValidTimestamp() throws IOException {
        ExtractedDocument document = TestUtils.loadDocument("logs/timestamp/valid-iso-format.log");
        List<LogEntry> entries = parser.parse(document);
        assertEquals(1, entries.size());
        testDebugLog(entries, 0);
    }

    @Test
    public void shouldSkipInvalidTimestampAndParseValidEntries() throws IOException {
        ExtractedDocument document = TestUtils.loadDocument("logs/timestamp/multiline-valid-invalid-iso-format.log");
        List<LogEntry> entries = parser.parse(document);
        assertEquals(2, entries.size());
        testDebugLog(entries, 0);
        testDebugLog(entries, 1);
    }

    @Test
    public void shouldSkipInvalidTimestampAndContinueParsing() throws IOException {
        ExtractedDocument document = TestUtils.loadDocument("logs/timestamp/invalid-continuation-line.log");
        List<LogEntry> entries = parser.parse(document);
        assertEquals(2, entries.size());
        testInfoLog(entries, 0);
        testInfoLog(entries, 1);
    }


}

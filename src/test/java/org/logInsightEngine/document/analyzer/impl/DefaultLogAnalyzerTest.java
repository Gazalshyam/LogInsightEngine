package org.logInsightEngine.document.analyzer.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.logInsightEngine.document.analyzer.*;
import org.logInsightEngine.dtos.result.*;
import org.logInsightEngine.model.domain.LogEntry;
import org.logInsightEngine.model.domain.LogLevel;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DefaultLogAnalyzerTest {
    private DefaultLogAnalyzer defaultLogAnalyzer;
    @Mock
    private SummaryAnalyzer summaryAnalyzer;
    @Mock
    private SeverityAnalyzer severityAnalyzer;
    @Mock
    private ErrorAnalyzer errorAnalyzer;
    @Mock
    private FindingAnalyzer findingAnalyzer;
    @Mock
    private CorrelationAnalyzer correlationAnalyzer;
    @Mock
    private TimelineAnalyzer timelineAnalyzer;

    private LogEntry createLogEntry(LogLevel level, String logger, String message, String thread, Instant timestamp) {
        LogEntry logEntry = new LogEntry();
        logEntry.setLevel(level);
        logEntry.setLogger(logger);
        logEntry.setMessage(message);
        logEntry.setThread(thread);
        logEntry.setTimestamp(timestamp);
        return logEntry;
    }

    @BeforeEach
    void setUp() {
        defaultLogAnalyzer = new DefaultLogAnalyzer(
                summaryAnalyzer,
                severityAnalyzer,
                errorAnalyzer,
                findingAnalyzer,
                timelineAnalyzer,
                correlationAnalyzer

        );
    }

    private List<LogEntry> createLogEntries() {
        return List.of(
                createLogEntry(LogLevel.INFO, "Application started", "The application has been started", "main", Instant.ofEpochSecond(100)),
                createLogEntry(LogLevel.ERROR, "Database connection failed", "Service failed to secure a database connection", "thread-T1234", Instant.ofEpochSecond(105)),
                createLogEntry(LogLevel.WARN, "Retrying connection", "Trying to connect to connector service", "main", Instant.ofEpochSecond(110))
        );
    }

    @Test
    public void shouldPopulateAnalysisResult() {
        List<LogEntry> logEntries = createLogEntries();
        Summary summary = new Summary();
        SeverityStatistics severityStatistics = new SeverityStatistics();
        ErrorGroup errorGroup = new ErrorGroup();
        ErrorAnalysisResult errorAnalysisResult = new ErrorAnalysisResult();
        errorAnalysisResult.setErrorGroups(List.of(errorGroup));
        Finding finding = new Finding();
        Correlation correlation = new Correlation();
        Timeline timeline = new Timeline();
        timeline.setEvents(List.of());
        when(summaryAnalyzer.analyze(logEntries)).thenReturn(summary);
        when(severityAnalyzer.analyze(logEntries)).thenReturn(severityStatistics);
        when(errorAnalyzer.analyze(logEntries)).thenReturn(errorAnalysisResult);
        when(findingAnalyzer.analyze(List.of(errorGroup))).thenReturn(List.of(finding));
        when(correlationAnalyzer.analyze(errorAnalysisResult)).thenReturn(List.of(correlation));
        when(timelineAnalyzer.analyze(logEntries, List.of(finding))).thenReturn(timeline);
        AnalysisResult result = defaultLogAnalyzer.analyze(logEntries);
        assertSame(summary, result.getSummary());
        assertSame(severityStatistics, result.getSeverityStatistics());
        assertEquals(List.of(errorGroup), result.getErrorGroups());
        assertEquals(List.of(finding), result.getFindings());
        assertEquals(List.of(correlation), result.getCorrelations());
        assertSame(timeline, result.getTimeline());
    }

    @Test
    public void analyze_shouldPassLogEntriesToAnalyzers() {
        List<LogEntry> logEntries = createLogEntries();
        ErrorGroup errorGroup = new ErrorGroup();
        ErrorAnalysisResult errorAnalysisResult = new ErrorAnalysisResult();
        errorAnalysisResult.setErrorGroups(List.of(errorGroup));
        Finding finding = new Finding();
        Correlation correlation = new Correlation();
        Timeline timeline = new Timeline();
        timeline.setEvents(List.of());
        when(errorAnalyzer.analyze(logEntries)).thenReturn(errorAnalysisResult);
        when(findingAnalyzer.analyze(List.of(errorGroup))).thenReturn(List.of(finding));
        when(correlationAnalyzer.analyze(errorAnalysisResult)).thenReturn(List.of(correlation));
        when(timelineAnalyzer.analyze(logEntries, List.of(finding))).thenReturn(timeline);
        when(summaryAnalyzer.analyze(logEntries)).thenReturn(new Summary());
        when(severityAnalyzer.analyze(logEntries)).thenReturn(new SeverityStatistics());
        defaultLogAnalyzer.analyze(logEntries);
        verify(summaryAnalyzer).analyze(logEntries);
        verify(severityAnalyzer).analyze(logEntries);
        verify(errorAnalyzer).analyze(logEntries);
        verify(findingAnalyzer).analyze(List.of(errorGroup));
        verify(correlationAnalyzer).analyze(errorAnalysisResult);
        verify(timelineAnalyzer).analyze(logEntries, List.of(finding));

    }

    @Test
    public void analyze_shouldPassErrorGroupsToFindingAnalyzer() {
        List<LogEntry> logEntries = createLogEntries();
        ErrorGroup errorGroup = new ErrorGroup();
        ErrorAnalysisResult errorAnalysisResult = new ErrorAnalysisResult();
        errorAnalysisResult.setErrorGroups(List.of(errorGroup));
        Finding finding = new Finding();
        when(errorAnalyzer.analyze(logEntries)).thenReturn(errorAnalysisResult);
        when(findingAnalyzer.analyze(List.of(errorGroup))).thenReturn(List.of(finding));
        defaultLogAnalyzer.analyze(logEntries);
        verify(findingAnalyzer).analyze(List.of(errorGroup));
    }

    @Test
    public void analyze_shouldPassFindingsToTimelineAnalyzer() {
        List<LogEntry> logEntries = createLogEntries();
        ErrorGroup errorGroup = new ErrorGroup();
        ErrorAnalysisResult errorAnalysisResult = new ErrorAnalysisResult();
        errorAnalysisResult.setErrorGroups(List.of(errorGroup));
        Finding finding = new Finding();
        Timeline timeline = new Timeline();
        timeline.setEvents(List.of());
        when(errorAnalyzer.analyze(logEntries)).thenReturn(errorAnalysisResult);
        when(findingAnalyzer.analyze(List.of(errorGroup))).thenReturn(List.of(finding));
        when(timelineAnalyzer.analyze(logEntries, List.of(finding))).thenReturn(timeline);
        defaultLogAnalyzer.analyze(logEntries);
        verify(findingAnalyzer).analyze(List.of(errorGroup));
        verify(timelineAnalyzer).analyze(logEntries, List.of(finding));

    }

    @Test
    public void analyze_shouldPassErrorAnalysisToCorrelationAnalyzer() {
        List<LogEntry> logEntries = createLogEntries();
        ErrorGroup errorGroup = new ErrorGroup();
        ErrorAnalysisResult errorAnalysisResult = new ErrorAnalysisResult();
        errorAnalysisResult.setErrorGroups(List.of(errorGroup));
        Correlation correlation = new Correlation();
        when(errorAnalyzer.analyze(logEntries)).thenReturn(errorAnalysisResult);
        when(correlationAnalyzer.analyze(errorAnalysisResult)).thenReturn(List.of(correlation));
        defaultLogAnalyzer.analyze(logEntries);
        verify(correlationAnalyzer).analyze(errorAnalysisResult);
    }

    @Test
    public void analyze_emptyLogEntries_shouldStillOrchestrate() {
        List<LogEntry> logEntries = List.of();
        ErrorGroup errorGroup = new ErrorGroup();
        ErrorAnalysisResult errorAnalysisResult = new ErrorAnalysisResult();
        errorAnalysisResult.setErrorGroups(List.of(errorGroup));
        Finding finding = new Finding();
        Correlation correlation = new Correlation();
        Timeline timeline = new Timeline();
        timeline.setEvents(List.of());
        when(errorAnalyzer.analyze(logEntries)).thenReturn(errorAnalysisResult);
        when(findingAnalyzer.analyze(List.of(errorGroup))).thenReturn(List.of(finding));
        when(correlationAnalyzer.analyze(errorAnalysisResult)).thenReturn(List.of(correlation));
        when(timelineAnalyzer.analyze(logEntries, List.of(finding))).thenReturn(timeline);
        when(summaryAnalyzer.analyze(logEntries)).thenReturn(new Summary());
        when(severityAnalyzer.analyze(logEntries)).thenReturn(new SeverityStatistics());
        defaultLogAnalyzer.analyze(logEntries);
        verify(summaryAnalyzer).analyze(logEntries);
        verify(severityAnalyzer).analyze(logEntries);
        verify(findingAnalyzer).analyze(List.of(errorGroup));
        verify(correlationAnalyzer).analyze(errorAnalysisResult);
        verify(timelineAnalyzer).analyze(logEntries, List.of(finding));
    }

}

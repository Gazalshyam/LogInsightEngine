package org.logInsightEngine.document.analyzer.impl;

import org.logInsightEngine.document.analyzer.*;
import org.logInsightEngine.dtos.result.*;
import org.logInsightEngine.model.domain.LogEntry;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DefaultLogAnalyzer implements LogAnalyzer {

    private final SummaryAnalyzer summaryAnalyzer;
    private final SeverityAnalyzer severityAnalyzer;
    private final ErrorAnalyzer errorAnalyzer;
    private final FindingAnalyzer findingAnalyzer;
    private final TimelineAnalyzer timelineAnalyzer;
    private final CorrelationAnalyzer correlationAnalyzer;

    public DefaultLogAnalyzer(SummaryAnalyzer summaryAnalyzer, SeverityAnalyzer severityAnalyzer, ErrorAnalyzer errorAnalyzer, FindingAnalyzer findingAnalyzer, TimelineAnalyzer timelineAnalyzer, CorrelationAnalyzer correlationAnalyzer) {
        this.summaryAnalyzer = summaryAnalyzer;
        this.severityAnalyzer = severityAnalyzer;
        this.errorAnalyzer = errorAnalyzer;
        this.findingAnalyzer = findingAnalyzer;
        this.correlationAnalyzer = correlationAnalyzer;
        this.timelineAnalyzer = timelineAnalyzer;
    }

    @Override
    public AnalysisResult analyze(List<LogEntry> logEntries) {

        Summary summary = summaryAnalyzer.analyze(logEntries);
        SeverityStatistics severityStatistics = severityAnalyzer.analyze(logEntries);
        ErrorAnalysisResult errorAnalysisResult = errorAnalyzer.analyze(logEntries);
        List<Finding> findings = findingAnalyzer.analyze(errorAnalysisResult.getErrorGroups());
        List<Correlation>  correlations = correlationAnalyzer.analyze(errorAnalysisResult);
        Timeline timeline = timelineAnalyzer.analyze(logEntries, findings);
        AnalysisResult result = new AnalysisResult();

        result.setSummary(summary);
        result.setSeverityStatistics(severityStatistics);
        result.setErrorGroups(errorAnalysisResult.getErrorGroups());
        result.setFindings(findings);
        result.setCorrelations(correlations);
        result.setTimeline(timeline);

        return result;
    }
}

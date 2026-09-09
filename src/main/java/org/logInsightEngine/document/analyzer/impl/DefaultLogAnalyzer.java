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

    public DefaultLogAnalyzer (SummaryAnalyzer summaryAnalyzer, SeverityAnalyzer severityAnalyzer, ErrorAnalyzer errorAnalyzer, FindingAnalyzer findingAnalyzer) {
        this.summaryAnalyzer = summaryAnalyzer;
        this.severityAnalyzer = severityAnalyzer;
        this.errorAnalyzer = errorAnalyzer;
        this.findingAnalyzer = findingAnalyzer;
    }

    @Override
    public AnalysisResult analyze(List<LogEntry> logEntries) {

        Summary summary = summaryAnalyzer.analyze(logEntries);
        SeverityStatistics severityStatistics = severityAnalyzer.analyze(logEntries);
        ErrorAnalysisResult errorAnalysis = errorAnalyzer.analyze(logEntries);
        List<Finding> findings = findingAnalyzer.analyze(errorAnalysis.getErrorGroups());
        AnalysisResult result = new AnalysisResult();

        result.setSummary(summary);
        result.setSeverityStatistics(severityStatistics);
        result.setErrorGroups(errorAnalysis.getErrorGroups());
        result.setFindings(findings);

        return result;
    }
}

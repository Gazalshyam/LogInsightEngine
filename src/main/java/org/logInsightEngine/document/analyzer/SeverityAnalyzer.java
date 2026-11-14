package org.logInsightEngine.document.analyzer;

import org.logInsightEngine.dtos.result.SeverityStatistics;
import org.logInsightEngine.model.domain.LogEntry;

import java.util.List;

public interface SeverityAnalyzer {
    SeverityStatistics analyze(List<LogEntry> logEntries);
}

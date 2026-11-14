package org.logInsightEngine.document.analyzer;

import org.logInsightEngine.dtos.result.Summary;
import org.logInsightEngine.model.domain.LogEntry;

import java.util.List;

public interface SummaryAnalyzer {
    Summary analyze(List<LogEntry> logEntries);

}

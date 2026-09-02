package org.logInsightEngine.document.analyzer;

import org.logInsightEngine.dtos.result.ErrorAnalysisResult;
import org.logInsightEngine.model.domain.LogEntry;

import java.util.List;

public interface ErrorAnalyzer {
    ErrorAnalysisResult analyze(List<LogEntry> logEntries);

}

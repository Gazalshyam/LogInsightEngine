package org.logInsightEngine.document.analyzer;

import org.logInsightEngine.dtos.result.AnalysisResult;
import org.logInsightEngine.model.domain.LogEntry;

import java.util.List;

public interface LogAnalyzer {
    AnalysisResult analyze(List<LogEntry> logEntries);

}

package org.logInsightEngine.document.analyzer;

import org.logInsightEngine.dtos.result.Correlation;
import org.logInsightEngine.dtos.result.ErrorAnalysisResult;
import org.logInsightEngine.model.domain.LogEntry;

import java.util.List;

public interface CorrelationAnalyzer {
    List<Correlation> analyze(List<LogEntry> logEntries, ErrorAnalysisResult errorAnalysisResult);
}

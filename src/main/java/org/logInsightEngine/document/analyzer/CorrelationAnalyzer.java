package org.logInsightEngine.document.analyzer;

import org.logInsightEngine.dtos.result.Correlation;
import org.logInsightEngine.dtos.result.ErrorAnalysisResult;

import java.util.List;

public interface CorrelationAnalyzer {
    List<Correlation> analyze(ErrorAnalysisResult errorAnalysisResult);
}

package org.logInsightEngine.document.ai.service;

import org.logInsightEngine.dtos.result.AIAnalysisResult;
import org.logInsightEngine.dtos.result.AnalysisResult;

public interface AIAnalysisService {
    AIAnalysisResult analyze(AnalysisResult result);
}

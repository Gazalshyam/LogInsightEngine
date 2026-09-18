package org.logInsightEngine.document.ai.service;

import org.logInsightEngine.dtos.context.AIAnalysisContext;
import org.logInsightEngine.dtos.result.AnalysisResult;

public interface AIContextBuilder {
    AIAnalysisContext build(AnalysisResult analysisResult);
}

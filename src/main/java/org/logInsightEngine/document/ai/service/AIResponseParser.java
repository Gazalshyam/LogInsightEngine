package org.logInsightEngine.document.ai.service;

import org.logInsightEngine.dtos.result.AIAnalysisResult;

public interface AIResponseParser {
    AIAnalysisResult parse(String response);
}

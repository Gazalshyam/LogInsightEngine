package org.logInsightEngine.document.ai.service;

import org.logInsightEngine.dtos.context.AIAnalysisContext;

public interface AIContextSerializer {
    String serialize(AIAnalysisContext context);
}

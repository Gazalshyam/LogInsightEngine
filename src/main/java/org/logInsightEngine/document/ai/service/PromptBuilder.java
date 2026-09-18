package org.logInsightEngine.document.ai.service;

import org.logInsightEngine.dtos.context.AIAnalysisContext;

public interface PromptBuilder {
    String buildSystemPrompt();
    String buildUserPrompt(String context);
}

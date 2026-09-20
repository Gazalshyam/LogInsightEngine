package org.logInsightEngine.document.ai.service;

public interface PromptBuilder {
    String buildSystemPrompt();

    String buildUserPrompt(String context);
}

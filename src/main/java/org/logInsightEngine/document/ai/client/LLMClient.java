package org.logInsightEngine.document.ai.client;

public interface LLMClient {
    String generate(String systemPrompt, String userPrompt);
}

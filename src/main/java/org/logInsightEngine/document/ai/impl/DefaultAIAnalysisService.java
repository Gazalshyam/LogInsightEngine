package org.logInsightEngine.document.ai.impl;

import org.logInsightEngine.document.ai.client.LLMClient;
import org.logInsightEngine.document.ai.service.*;
import org.logInsightEngine.dtos.context.AIAnalysisContext;
import org.logInsightEngine.dtos.result.AIAnalysisResult;
import org.logInsightEngine.dtos.result.AnalysisResult;
import org.springframework.stereotype.Component;

@Component
public class DefaultAIAnalysisService implements AIAnalysisService {

    private final AIContextBuilder contextBuilder;
    private final AIContextSerializer contextSerializer;
    private final PromptBuilder promptBuilder;
    private final LLMClient llmClient;
    private final AIResponseParser responseParser;

    public DefaultAIAnalysisService(AIContextBuilder contextBuilder, AIContextSerializer contextSerializer, PromptBuilder promptBuilder, LLMClient llmClient, AIResponseParser responseParser) {

        this.contextBuilder = contextBuilder;
        this.contextSerializer = contextSerializer;
        this.promptBuilder = promptBuilder;
        this.llmClient = llmClient;
        this.responseParser = responseParser;
    }

    @Override
    public AIAnalysisResult analyze(AnalysisResult analysisResult) {
        AIAnalysisContext context = contextBuilder.build(analysisResult);
        String serializedContext = contextSerializer.serialize(context);
        String systemPrompt = promptBuilder.buildSystemPrompt();
        String userPrompt = promptBuilder.buildUserPrompt(serializedContext);
        String response = llmClient.generate(systemPrompt, userPrompt);
        return responseParser.parse(response);
    }
}

package org.logInsightEngine.document.ai.impl;

import org.logInsightEngine.document.ai.service.AIContextBuilder;
import org.logInsightEngine.dtos.context.AIAnalysisContext;
import org.logInsightEngine.dtos.result.AnalysisResult;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class DefaultAIContextBuilder implements AIContextBuilder {

    @Override
    public AIAnalysisContext build(AnalysisResult analysisResult){

        Objects.requireNonNull(analysisResult, "analysisResult cannot be null");
        AIAnalysisContext aiAnalysisContext = new AIAnalysisContext();
        aiAnalysisContext.setCorrelations(analysisResult.getCorrelations());
        aiAnalysisContext.setErrorGroups(analysisResult.getErrorGroups());
        aiAnalysisContext.setFindings(analysisResult.getFindings());
        aiAnalysisContext.setSeverityStatistics(analysisResult.getSeverityStatistics());
        aiAnalysisContext.setSummary(analysisResult.getSummary());
        aiAnalysisContext.setTimeline(analysisResult.getTimeline());

        return aiAnalysisContext;
    }
}

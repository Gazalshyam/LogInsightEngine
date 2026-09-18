package org.logInsightEngine.document.ai.impl;

import org.junit.jupiter.api.Test;
import org.logInsightEngine.dtos.context.AIAnalysisContext;
import org.logInsightEngine.dtos.result.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;

public class DefaultAIContextBuilderTest {
    private final DefaultAIContextBuilder defaultAIContextBuilder = new DefaultAIContextBuilder();
    @Test
    void shouldBuildContextFromAnalysisResult() {

        Summary summary = new Summary();
        SeverityStatistics severityStatistics = new SeverityStatistics();
        List<ErrorGroup> errorGroups = List.of(new ErrorGroup());
        List<Finding> findings = List.of(new Finding());
        List<Correlation> correlations = List.of(new Correlation());
        Timeline timeline = new Timeline();

        AnalysisResult analysisResult = new AnalysisResult();

        analysisResult.setSummary(summary);
        analysisResult.setSeverityStatistics(severityStatistics);
        analysisResult.setErrorGroups(errorGroups);
        analysisResult.setFindings(findings);
        analysisResult.setCorrelations(correlations);
        analysisResult.setTimeline(timeline);
        AIAnalysisContext context = new DefaultAIContextBuilder().build(analysisResult);
        assertSame(summary, context.getSummary());
        assertSame(severityStatistics, context.getSeverityStatistics());
        assertSame(errorGroups, context.getErrorGroups());
        assertSame(findings, context.getFindings());
        assertSame(correlations, context.getCorrelations());
        assertSame(timeline, context.getTimeline());
    }
}

package org.logInsightEngine.document.ai.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DefaultPromptBuilderTest {
    private final DefaultPromptBuilder promptBuilder = new DefaultPromptBuilder();

    @Test
    void shouldBuildSystemPrompt() {
        String prompt = promptBuilder.buildSystemPrompt();
        assertTrue(prompt.contains("incident analysis assistant"));
    }

    @Test
    void shouldContainImportantSemantics() {
        String prompt = promptBuilder.buildSystemPrompt();
        assertTrue(prompt.contains("ErrorGroups"));
        assertTrue(prompt.contains("Findings"));
        assertTrue(prompt.contains("PRECEDES"));
        assertTrue(prompt.contains("RELATED_ERROR"));
        assertTrue(prompt.contains("SIMILAR"));
        assertTrue(prompt.contains("Summary"));
        assertTrue(prompt.contains("SeverityStatistics"));
        assertTrue(prompt.contains("Timeline"));
    }

    @Test
    void shouldContainReasoningAndOutputContract() {
        String prompt = promptBuilder.buildSystemPrompt();
        assertTrue(prompt.contains("Correlation does not establish causation"));
        assertTrue(prompt.contains("\"probableRootCause\""));
        assertTrue(prompt.contains("\"contributingFactors\""));
        assertTrue(prompt.contains("\"affectedComponents\""));
        assertTrue(prompt.contains("\"recommendedInvestigations\""));
        assertTrue(prompt.contains("\"caveats\""));


    }

    @Test
    void shouldIncludeSerializedContext() {

        String context = """
                  {
                  "summary": {
                    "totalLogEntries": 7
                  }
                }
                """;

        String prompt = promptBuilder.buildUserPrompt(context);
        assertTrue(prompt.contains(context));
        assertTrue(prompt.contains("Analyze the following incident."));
        assertTrue(prompt.contains("Use only the provided evidence."));
    }

    @Test
    void shouldRejectNullContext() {
        assertThrows(NullPointerException.class, () -> promptBuilder.buildUserPrompt(null));
    }

    @Test
    void shouldRejectEmptyContext() {
        assertThrows(IllegalArgumentException.class, () -> promptBuilder.buildUserPrompt(""));
    }

    @Test
    void shouldRejectSpaceContext() {
        assertThrows(IllegalArgumentException.class, () -> promptBuilder.buildUserPrompt(" "));
    }

    @Test
    void shouldPlaceContextInIncidentAnalysisSection() {

        String context = "{\"summary\":{\"totalLogEntries\":7}}";

        String prompt = promptBuilder.buildUserPrompt(context);

        int markerIndex =
                prompt.indexOf("Incident analysis context:");

        int contextIndex =
                prompt.indexOf(context);

        assertTrue(markerIndex >= 0);
        assertTrue(contextIndex > markerIndex);
    }

}

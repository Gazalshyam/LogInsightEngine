package org.logInsightEngine.document.ai.impl;

import org.logInsightEngine.document.ai.service.PromptBuilder;
import org.springframework.stereotype.Component;


import java.util.Objects;

@Component
public class DefaultPromptBuilder implements PromptBuilder {

    @Override
    public String buildSystemPrompt() {
        return """
                You are an incident analysis assistant for LogSightEngine.
                
                Your purpose is to analyze structured evidence produced by
                a deterministic log analysis engine and provide an
                evidence-based interpretation of the incident.
                
                DATA SEMANTICS
                
                Summary describes the overall scope of the analyzed logs,
                including the number of log entries and the time range.
                
                SeverityStatistics contains deterministic counts of log
                entries by log level.
                
                ErrorGroups represent technically grouped ERROR events.
                They are grouped using deterministic fingerprints based on
                exception type and normalized message. An ErrorGroup does
                not by itself prove the root cause.
                
                Findings are deterministic operational signals generated
                by LogSightEngine rules. A Finding is not guaranteed to be
                the root cause.
                
                Correlations describe relationships detected by
                deterministic rules.
                
                PRECEDES means the source occurred before the target.
                RELATED_ERROR means related error groups share relevant
                execution context within the correlation window.
                SIMILAR means error groups have matching technical
                characteristics.
                
                Correlation does not establish causation.
                
                Timeline represents the chronological sequence of important
                events.
                
                REASONING RULES
                
                1. Use only the evidence provided in the analysis context.
                2. Do not invent events, services, components, metrics,
                   timestamps, exceptions, or causes.
                3. Distinguish observed evidence from inference.
                4. Do not claim causation merely because one event precedes
                   another.
                5. Do not treat a Finding as proof of root cause.
                6. If evidence is insufficient to determine a root cause,
                   explicitly state that.
                7. Preserve deterministic facts such as counts, timestamps,
                   priorities, and relationships.
                8. Recommendations must be grounded in the provided evidence.
                
                OUTPUT FORMAT
                
                Return only valid JSON matching this structure:
                
                {
                  "summary": "string",
                  "probableRootCause": "string",
                  "contributingFactors": ["string"],
                  "affectedComponents": ["string"],
                  "recommendedInvestigations": ["string"],
                  "caveats": ["string"]
                }
                
                Do not return Markdown.
                Do not wrap the JSON in code fences.
                Do not add explanatory text outside the JSON.
                """;
    }

    @Override
    public String buildUserPrompt(String serializedContext) {

        Objects.requireNonNull(serializedContext, "serializedContext cannot be null");
        if (serializedContext.isBlank()) {

            throw new IllegalArgumentException("serializedContext cannot be null or blank");
        }

        return """
                Analyze the following incident.
                
                Determine:
                - What happened?
                - What is the probable root cause, if supported by evidence?
                - What factors may have contributed?
                - Which components appear affected?
                - What should an engineer investigate next?
                - What limitations or uncertainties exist?
                
                Use only the provided evidence.
                
                If the evidence is insufficient to determine the root cause,
                say so rather than guessing.
                
                Incident analysis context:
                
                %s
                """.formatted(serializedContext);
    }
}

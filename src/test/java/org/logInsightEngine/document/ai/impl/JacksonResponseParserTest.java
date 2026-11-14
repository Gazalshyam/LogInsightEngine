package org.logInsightEngine.document.ai.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.logInsightEngine.dtos.result.AIAnalysisResult;
import org.logInsightEngine.exception.AIResponseParsingException;

import static org.junit.jupiter.api.Assertions.*;

public class JacksonResponseParserTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JacksonResponseParser jacksonResponseParser = new JacksonResponseParser(objectMapper);


    private String getContextString() {
        return "{\n" +
                "  \"summary\": \"Database connection failures occurred.\",\n" +
                "  \"probableRootCause\": \"Connection pool exhaustion\",\n" +
                "  \"contributingFactors\": [\n" +
                "    \"High connection demand\"\n" +
                "  ],\n" +
                "  \"affectedComponents\": [\n" +
                "    \"database\"\n" +
                "  ],\n" +
                "  \"recommendedInvestigations\": [\n" +
                "    \"Inspect HikariCP metrics\"\n" +
                "  ],\n" +
                "  \"caveats\": []\n" +
                "}";
    }

    private String getMalformedContextString() {
        return "{\n" +
                "  \"summaries\": \"Database connection failures occurred.\",\n" +
                "  \"probableRootCause\": \"Connection pool exhaustion\",\n" +
                "  \"contributingFactors\": {\n" +
                "    \"High connection demand\"\n" +
                "  }\n" +
                "  \"affectedComponents\": [\n" +
                "    \"database\"\n" +
                "  ],\n" +
                "  \"recommendedInvestigations\": [\n" +
                "    \"Inspect HikariCP metrics\"\n" +
                "  ],\n" +
                "  \"caveats\": []\n" +
                "}";
    }

    private String getMissingOptionalContextString() {
        return "{\n" +
                "  \"summary\": \"Database connection failures occurred.\",\n" +
                "  \"probableRootCause\": \"Connection pool exhaustion\",\n" +
                "  \"contributingFactors\": [\n" +
                "    \"High connection demand\"\n" +
                "  ],\n" +
                "  \"affectedComponents\": [\n" +
                "    \"database\"\n" +
                "  ],\n" +
                "  \"recommendedInvestigations\": [\n" +
                "    \"Inspect HikariCP metrics\"\n" +
                "  ]\n" +
                "}";
    }

    private String getUnknownContextString() {
        return "{\n" +
                "  \"summary\": \"Database connection failures occurred.\",\n" +
                "  \"probableRootCause\": \"Connection pool exhaustion\",\n" +
                "  \"contributingFactors\": [\n" +
                "    \"High connection demand\"\n" +
                "  ],\n" +
                "  \"affectedComponents\": [\n" +
                "    \"database\"\n" +
                "  ],\n" +
                "  \"recommendedInvestigations\": [\n" +
                "    \"Inspect HikariCP metrics\"\n" +
                "  ],\n" +
                "  \"caveats\": [],\n" +
                " \"unknown_field\":[]\n" +
                "}";
    }

    @Test
    public void shouldParseValidJson() {
        AIAnalysisResult analysisResult = jacksonResponseParser.parse(getContextString());
        assertEquals("Database connection failures occurred.", analysisResult.getSummary());
        assertEquals("Connection pool exhaustion", analysisResult.getProbableRootCause());
        assertEquals("High connection demand", analysisResult.getContributingFactors().getFirst());
        assertEquals("database", analysisResult.getAffectedComponents().getFirst());
        assertEquals("Inspect HikariCP metrics", analysisResult.getRecommendedInvestigations().getFirst());
        assertEquals(0, analysisResult.getCaveats().size());
    }

    @Test
    public void shouldRejectNullResponse() {
        assertThrows(AIResponseParsingException.class, () -> jacksonResponseParser.parse(null));
    }

    @Test
    public void shouldRejectBlankResponse() {
        assertThrows(AIResponseParsingException.class, () -> jacksonResponseParser.parse(""));

    }

    @Test
    public void shouldRejectMalformedJson() {
        assertThrows(AIResponseParsingException.class, () -> jacksonResponseParser.parse(getMalformedContextString()));

    }

    @Test
    public void shouldParseJsonWithMissingOptionalFields() {

        AIAnalysisResult analysisResult = jacksonResponseParser.parse(getMissingOptionalContextString());
        assertEquals("Database connection failures occurred.", analysisResult.getSummary());
        assertEquals("Connection pool exhaustion", analysisResult.getProbableRootCause());
        assertEquals("High connection demand", analysisResult.getContributingFactors().getFirst());
        assertEquals("database", analysisResult.getAffectedComponents().getFirst());
        assertEquals("Inspect HikariCP metrics", analysisResult.getRecommendedInvestigations().getFirst());
        assertNull(analysisResult.getCaveats());
    }

    @Test
    public void shouldIgnoreUnknownFields() {

        AIAnalysisResult analysisResult = jacksonResponseParser.parse(getUnknownContextString());
        assertEquals("Database connection failures occurred.", analysisResult.getSummary());
        assertEquals("Connection pool exhaustion", analysisResult.getProbableRootCause());
        assertEquals("High connection demand", analysisResult.getContributingFactors().getFirst());
        assertEquals("database", analysisResult.getAffectedComponents().getFirst());
        assertEquals("Inspect HikariCP metrics", analysisResult.getRecommendedInvestigations().getFirst());
        assertEquals(0, analysisResult.getCaveats().size());
    }
}


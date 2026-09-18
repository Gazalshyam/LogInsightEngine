package org.logInsightEngine.document.ai.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.logInsightEngine.document.ai.service.AIResponseParser;
import org.logInsightEngine.dtos.result.AIAnalysisResult;
import org.logInsightEngine.exception.AIResponseParsingException;
import org.springframework.stereotype.Component;

@Component
public class JacksonResponseParser implements AIResponseParser {

        private final ObjectMapper objectMapper;

        public JacksonResponseParser(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        public AIAnalysisResult parse(String response) {

            if (response == null || response.isBlank()) {
                throw new AIResponseParsingException("AI response is empty");
            }

            try {
                return objectMapper.readValue( response, AIAnalysisResult.class);
            } catch (JsonProcessingException e) {
                throw new AIResponseParsingException("Failed to parse AI response", e);
            }
        }
}

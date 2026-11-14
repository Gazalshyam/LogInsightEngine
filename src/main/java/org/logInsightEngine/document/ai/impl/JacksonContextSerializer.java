package org.logInsightEngine.document.ai.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.logInsightEngine.document.ai.service.AIContextSerializer;
import org.logInsightEngine.dtos.context.AIAnalysisContext;
import org.logInsightEngine.exception.AIContextSerializationException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class JacksonContextSerializer implements AIContextSerializer {

    private final ObjectMapper objectMapper;

    public JacksonContextSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String serialize(AIAnalysisContext context) {
        Objects.requireNonNull(context, "context cannot be null");

        try {
            return objectMapper.writeValueAsString(context);
        } catch (JsonProcessingException e) {
            throw new AIContextSerializationException("Failed to serialize AI analysis context", e);
        }
    }


}

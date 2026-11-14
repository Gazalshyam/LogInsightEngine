package org.logInsightEngine.model.env;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "openai")
public record OpenAIProperties(
        String apiKey,
        String model,
        Duration timeout,
        Double temperature,
        Integer maxOutputTokens
) {
}

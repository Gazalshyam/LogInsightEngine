package org.logInsightEngine.document.ai.client;

import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import org.logInsightEngine.exception.LLMClientException;
import org.logInsightEngine.model.env.OpenAIProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.openai.client.OpenAIClient;

import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class OpenAIClientAdapter implements LLMClient {

    private final OpenAIClient client;
    private final OpenAIProperties properties;

    @Autowired
    public OpenAIClientAdapter(OpenAIProperties properties) {
        this(properties,   OpenAIOkHttpClient.builder().apiKey(properties.apiKey()).timeout(properties.timeout()).build());
    }
    OpenAIClientAdapter(OpenAIProperties properties, OpenAIClient client) {
        this.properties = properties;
        this.client = client;
    }
    @Override
    public String generate(String systemPrompt,  String userPrompt) {


        Objects.requireNonNull(systemPrompt,"systemPrompt cannot be null");
        Objects.requireNonNull(userPrompt, "userPrompt cannot be null");
        if(userPrompt.isBlank()){
            throw new IllegalArgumentException("User Prompt cannot be blank");
        }
        if(systemPrompt.isBlank()){
            throw new IllegalArgumentException("System Prompt cannot be blank");
        }

        try {
            ResponseCreateParams params = ResponseCreateParams.builder().model(properties.model()).input(userPrompt).instructions(systemPrompt).temperature(properties.temperature()).maxOutputTokens(properties.maxOutputTokens().longValue()).build();
            Response response = client.responses().create(params);
            String output =  response.output().stream().flatMap(item -> item.message().stream()).flatMap(message -> message.content().stream()).flatMap(content -> content.outputText().stream()).map(outputText -> outputText.text()).collect(Collectors.joining());
            if (output.isBlank()) {
                throw new LLMClientException("OpenAI returned an empty response", null);
            }
            return output;
        } catch (LLMClientException e) {
            throw e;
        }catch(Exception e){
            throw new LLMClientException("Failed to generate response from OpenAI", e);
        }
        }

}

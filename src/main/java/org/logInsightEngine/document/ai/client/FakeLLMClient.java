package org.logInsightEngine.document.ai.client;

import org.springframework.stereotype.Component;


public class FakeLLMClient implements  LLMClient{

    private final String response;

    public FakeLLMClient(String response) {
        this.response = response;
    }

    @Override
    public String generate(String systemPrompt, String userPrompt){
        return response;
    }

}

package org.logInsightEngine.document.ai.client;

import com.openai.client.OpenAIClient;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.services.blocking.ResponseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.logInsightEngine.exception.LLMClientException;
import org.logInsightEngine.model.env.OpenAIProperties;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.openai.models.responses.Response;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class OpenAiClientAdapterTest {

    @Mock
    private OpenAIClient openAIClient;

    @Mock
    private ResponseService responses;
    String apiKey = (System.getenv("OPEN_API_KEY")== null || System.getenv("OPEN_API_KEY").isBlank()) ? "test_api_key": System.getenv("OPEN_API_KEY");
     OpenAIProperties properties = new OpenAIProperties(apiKey, "gpt-5.6", Duration.ofSeconds(30), 0.0, 2000);
    private final OpenAIClientAdapter adapter = new OpenAIClientAdapter(properties);
    @Test
    public void shouldRejectNullSystemPrompt(){
        assertThrows(NullPointerException.class, () -> adapter.generate(null, "hi"));
    }

    @Test
    public void shouldRejectEmptySystemPrompt(){
        assertThrows(IllegalArgumentException.class, () -> adapter.generate("", "hi"));
    }

    @Test
    public void shouldRejectNullUserPrompt(){
        assertThrows(NullPointerException.class, () -> adapter.generate("hi", null));
    }

    @Test
    public void shouldRejectEmptyUserPrompt(){
        assertThrows(IllegalArgumentException.class, () -> adapter.generate("hi", ""));

    }

}

package org.logInsightEngine.document.ai.client;

import org.junit.jupiter.api.Test;
import org.logInsightEngine.model.env.OpenAIProperties;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OpenAPIClientIT {


    @Test
    void shouldCallOpenAI() {

        OpenAIProperties properties = new OpenAIProperties(
                System.getenv("OPENAI_API_KEY"),
                "gpt-5.6",
                Duration.ofSeconds(30),
                0.0,
                500
        );

        OpenAIClientAdapter adapter =
                new OpenAIClientAdapter(properties);

        String response = adapter.generate(
                "Return only valid JSON.",
                "Say hello in JSON."
        );

        assertNotNull(response);
        assertFalse(response.isBlank());
    }

//     @Test
//    void shouldSuccessfullyConstructRequest() {
//
//        Response response = mock(Response.class);
//        when(responses.create(any(ResponseCreateParams.class))).thenReturn(response);
//        when(response.output()).thenReturn(List.of());
//        adapter.generate("system prompt", "user prompt");
//        ArgumentCaptor<ResponseCreateParams> captor = ArgumentCaptor.forClass(ResponseCreateParams.class);
//        verify(responses).create(captor.capture());
//        ResponseCreateParams params = captor.getValue();
//        assertEquals("gpt-5.6", params.model().toString());
//        assertEquals("user prompt", params.input().toString());
//        assertEquals("system prompt", params.instructions().toString());
//        assertEquals(0.0, params.temperature());
//        assertEquals(2000L, params.maxOutputTokens());
//    }


//     @Test
//    void shouldExtractResponse() {
//
//        Response response = mock(Response.class);
//        // mock output item
//        // mock message
//        // mock content
//        // mock output text
//        when(responses.create(any(ResponseCreateParams.class))).thenReturn(response);
//        // Configure mocked response to eventually return:
//        // "AI analysis response"
//        String result = adapter.generate( "system prompt","user prompt" );
//        assertEquals("AI analysis response", result);
//    }

//    @Test
//    void shouldHandleEmptyResponse() {
//
//        Response response = mock(Response.class);
//        when(responses.create(any(ResponseCreateParams.class))).thenReturn(response);
//        when(response.output()).thenReturn(List.of());
//        LLMClientException exception = assertThrows(LLMClientException.class, () -> adapter.generate("system prompt", "user prompt"));
//        assertEquals("OpenAI returned an empty response", exception.getMessage());
//    }

//    @Test
//    void shouldConvertAPIFailureToLLMClientException() {
//        when(responses.create(any(ResponseCreateParams.class))).thenThrow(new RuntimeException("API unavailable"));
//        LLMClientException exception = assertThrows( LLMClientException.class, () -> adapter.generate("system prompt", "user prompt" ));
//        assertEquals("Failed to generate response from OpenAI", exception.getMessage());
//        assertNotNull(exception.getCause());
//        assertEquals("API unavailable", exception.getCause().getMessage());
//    }
}

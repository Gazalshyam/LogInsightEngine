package org.logInsightEngine.exception;

public class LLMClientException extends RuntimeException{
    public LLMClientException(String message, Throwable cause){
        super(message, cause);
    }
}

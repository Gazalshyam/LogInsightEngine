package org.logInsightEngine.exception;

public class AIResponseParsingException extends RuntimeException {
    public AIResponseParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public AIResponseParsingException(String message) {
        super(message);
    }
}

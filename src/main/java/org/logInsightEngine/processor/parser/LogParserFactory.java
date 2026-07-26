package org.logInsightEngine.processor.parser;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LogParserFactory {
    private final List<LogParser> parsers;

    public LogParserFactory(List<LogParser> parsers) {
        this.parsers = parsers;
    }

    public LogParser getAvailableParsers(String message) {
        for (LogParser parser : parsers) {
            if (parser.supports(message)) {
                return parser;
            }
        }
        throw new UnsupportedOperationException("Unsupported file type: " + message);
    }
}

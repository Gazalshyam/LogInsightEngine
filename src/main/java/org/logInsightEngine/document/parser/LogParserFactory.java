package org.logInsightEngine.document.parser;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LogParserFactory {
    private final List<LogParser> parsers;

    public LogParserFactory(List<LogParser> parsers) {
        this.parsers = parsers;
    }

    public LogParser getParser(String content) {
        if (content == null || content.isEmpty()) {
            throw new IllegalArgumentException("Content cannot be null or empty.");
        }
        for (LogParser parser : parsers) {
            if (parser.supports(content)) {
                return parser;
            }
        }
        throw new UnsupportedOperationException("No parser found for the supplied document format.");
    }
}

package org.logInsightEngine.document.parser;

import org.logInsightEngine.exception.UnsupportedLogFormatException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class LogParserFactory {
    private final List<LogParser> parsers;
    private static final int MAX_DETECTION_LINES = 20;

    public LogParserFactory(List<LogParser> parsers) {
        this.parsers = parsers;
    }

    public LogParser getParser(String content) {
        if (!StringUtils.hasText(content)) {
            throw new IllegalArgumentException("Content cannot be null or empty.");
        }
        List<String> candidateLines = content.lines().filter(StringUtils::hasText).limit(MAX_DETECTION_LINES).toList();

        for (LogParser parser : parsers) {
            if (parser.supports(candidateLines)) {
                return parser;
            }
        }
        throw new UnsupportedLogFormatException("The log format is not supported for the provided content. ");
    }
}
